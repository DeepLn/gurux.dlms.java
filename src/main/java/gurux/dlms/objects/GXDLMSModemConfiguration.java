//
// --------------------------------------------------------------------------
//  Gurux Ltd
// 
//
//
// Filename:        $HeadURL$
//
// Version:         $Revision$,
//                  $Date$
//                  $Author$
//
// Copyright (c) Gurux Ltd
//
//---------------------------------------------------------------------------
//
//  DESCRIPTION
//
// This file is a part of Gurux Device Framework.
//
// Gurux Device Framework is Open Source software; you can redistribute it
// and/or modify it under the terms of the GNU General Public License 
// as published by the Free Software Foundation; version 2 of the License.
// Gurux Device Framework is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// More information of Gurux products: http://www.gurux.org
//
// This code is licensed under the GNU General Public License v2. 
// Full text may be retrieved at http://www.gnu.org/licenses/gpl-2.0.txt
//---------------------------------------------------------------------------

package gurux.dlms.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import gurux.dlms.GXByteBuffer;
import gurux.dlms.GXDLMSClient;
import gurux.dlms.GXDLMSSettings;
import gurux.dlms.enums.DataType;
import gurux.dlms.enums.ObjectType;
import gurux.dlms.internal.GXCommon;
import gurux.dlms.objects.enums.BaudRate;

public class GXDLMSModemConfiguration extends GXDLMSObject
        implements IGXDLMSBase {
    private GXDLMSModemInitialisation[] initialisationStrings;
    private String[] modemProfile;
    private BaudRate communicationSpeed;

    static final String[] defultProfiles() {
        return new String[] { "OK", "CONNECT", "RING", "NO CARRIER", "ERROR",
                "CONNECT 1200", "NO DIAL TONE", "BUSY", "NO ANSWER",
                "CONNECT 600", "CONNECT 2400", "CONNECT 4800", "CONNECT 9600",
                "CONNECT 14 400", "CONNECT 28 800", "CONNECT 33 600",
                "CONNECT 56 000" };
    }

    /**
     * Constructor.
     */
    public GXDLMSModemConfiguration() {
        super(ObjectType.MODEM_CONFIGURATION, "0.0.2.0.0.255", 0);
        initialisationStrings = new GXDLMSModemInitialisation[0];
        communicationSpeed = BaudRate.BAUDRATE_300;
        modemProfile = defultProfiles();
    }

    /**
     * Constructor.
     * 
     * @param ln
     *            Logical Name of the object.
     */
    public GXDLMSModemConfiguration(final String ln) {
        super(ObjectType.MODEM_CONFIGURATION, ln, 0);
        initialisationStrings = new GXDLMSModemInitialisation[0];
        communicationSpeed = BaudRate.BAUDRATE_300;
        modemProfile = defultProfiles();
    }

    /**
     * Constructor.
     * 
     * @param ln
     *            Logical Name of the object.
     * @param sn
     *            Short Name of the object.
     */
    public GXDLMSModemConfiguration(final String ln, final int sn) {
        super(ObjectType.MODEM_CONFIGURATION, ln, 0);
        initialisationStrings = new GXDLMSModemInitialisation[0];
        communicationSpeed = BaudRate.BAUDRATE_300;
        modemProfile = defultProfiles();
    }

    public final BaudRate getCommunicationSpeed() {
        return communicationSpeed;
    }

    public final void setCommunicationSpeed(final BaudRate value) {
        communicationSpeed = value;
    }

    public final GXDLMSModemInitialisation[] getInitialisationStrings() {
        return initialisationStrings;
    }

    public final void
            setInitialisationStrings(final GXDLMSModemInitialisation[] value) {
        initialisationStrings = value;
    }

    public final String[] getModemProfile() {
        return modemProfile;
    }

    public final void setModemProfile(final String[] value) {
        modemProfile = value;
    }

    @Override
    public final Object[] getValues() {
        return new Object[] { getLogicalName(), getCommunicationSpeed(),
                getInitialisationStrings(), getModemProfile() };
    }

    /*
     * Returns collection of attributes to read. If attribute is static and
     * already read or device is returned HW error it is not returned.
     */
    @Override
    public final int[] getAttributeIndexToRead() {
        java.util.ArrayList<Integer> attributes =
                new java.util.ArrayList<Integer>();
        // LN is static and read only once.
        if (getLogicalName() == null || getLogicalName().compareTo("") == 0) {
            attributes.add(1);
        }
        // CommunicationSpeed
        if (!isRead(2)) {
            attributes.add(2);
        }
        // InitialisationStrings
        if (!isRead(3)) {
            attributes.add(3);
        }
        // ModemProfile
        if (!isRead(4)) {
            attributes.add(4);
        }
        return GXDLMSObjectHelpers.toIntArray(attributes);
    }

    /*
     * Returns amount of attributes.
     */
    @Override
    public final int getAttributeCount() {
        return 4;
    }

    /*
     * Returns amount of methods.
     */
    @Override
    public final int getMethodCount() {
        return 0;
    }

    @Override
    public final DataType getDataType(final int index) {
        if (index == 1) {
            return DataType.OCTET_STRING;
        }
        if (index == 2) {
            return DataType.ENUM;
        }
        if (index == 3) {
            return DataType.ARRAY;
        }
        if (index == 4) {
            return DataType.ARRAY;
        }
        throw new IllegalArgumentException(
                "getDataType failed. Invalid attribute index.");
    }

    /*
     * Returns value of given attribute.
     */
    @Override
    public final Object getValue(final GXDLMSSettings settings, final int index,
            final int selector, final Object parameters) {
        if (index == 1) {
            return getLogicalName();
        }
        if (index == 2) {
            return communicationSpeed.ordinal();
        }
        if (index == 3) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.ARRAY.getValue());
            // Add count
            int cnt = 0;
            if (initialisationStrings != null) {
                cnt = initialisationStrings.length;
            }
            GXCommon.setObjectCount(cnt, data);
            if (cnt != 0) {
                for (GXDLMSModemInitialisation it : initialisationStrings) {
                    data.setUInt8(DataType.STRUCTURE.getValue());
                    data.setUInt8(3); // Count
                    GXCommon.setData(data, DataType.OCTET_STRING,
                            GXCommon.getBytes(it.getRequest()));
                    GXCommon.setData(data, DataType.OCTET_STRING,
                            GXCommon.getBytes(it.getResponse()));
                    GXCommon.setData(data, DataType.UINT16, it.getDelay());
                }
            }
            return data.array();
        }
        if (index == 4) {
            GXByteBuffer data = new GXByteBuffer();
            data.setUInt8(DataType.ARRAY.getValue());
            // Add count
            int cnt = 0;
            if (modemProfile != null) {
                cnt = modemProfile.length;
            }
            GXCommon.setObjectCount(cnt, data);
            if (cnt != 0) {
                for (String it : modemProfile) {
                    GXCommon.setData(data, DataType.OCTET_STRING,
                            GXCommon.getBytes(it));
                }
            }
            return data.array();
        }
        throw new IllegalArgumentException(
                "GetValue failed. Invalid attribute index.");
    }

    /*
     * Set value of given attribute.
     */
    @Override
    public final void setValue(final GXDLMSSettings settings, final int index,
            final Object value) {
        if (index == 1) {
            super.setValue(settings, index, value);
        } else if (index == 2) {
            communicationSpeed = BaudRate.values()[((Number) value).intValue()];
        } else if (index == 3) {
            initialisationStrings = null;
            if (value != null) {
                List<GXDLMSModemInitialisation> items =
                        new ArrayList<GXDLMSModemInitialisation>();
                for (Object it : (Object[]) value) {
                    GXDLMSModemInitialisation item =
                            new GXDLMSModemInitialisation();
                    item.setRequest(
                            GXDLMSClient.changeType((byte[]) Array.get(it, 0),
                                    DataType.STRING).toString());
                    item.setResponse(
                            GXDLMSClient.changeType((byte[]) Array.get(it, 1),
                                    DataType.STRING).toString());
                    if (Array.getLength(it) > 2) {
                        item.setDelay(((Number) Array.get(it, 2)).intValue());
                    }
                    items.add(item);
                }
                initialisationStrings = items
                        .toArray(new GXDLMSModemInitialisation[items.size()]);
            }
        } else if (index == 4) {
            modemProfile = null;
            if (value != null) {
                List<String> items = new ArrayList<String>();
                for (Object it : (Object[]) value) {
                    items.add(GXDLMSClient
                            .changeType((byte[]) it, DataType.STRING)
                            .toString());
                }
                modemProfile = items.toArray(new String[items.size()]);
            }
        } else {
            throw new IllegalArgumentException(
                    "GetValue failed. Invalid attribute index.");
        }
    }
}
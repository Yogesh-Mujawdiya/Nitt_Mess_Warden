package com.my.nitt_mess_warden.Class;

public class LeaveRequest {
    String Id, UserRollNo, From, To, Reason;

    public LeaveRequest(String id, String userRollNo, String from, String to, String reason) {
        Id = id;
        UserRollNo = userRollNo;
        From = from;
        To = to;
        Reason = reason;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserRollNo() {
        return UserRollNo;
    }

    public void setUserRollNo(String userRollNo) {
        UserRollNo = userRollNo;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }
}

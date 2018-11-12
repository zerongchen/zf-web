package com.aotain.zongfen.model.general;

import java.io.Serializable;

public class TriggerRelation implements Serializable {
    private Integer triggerId;

    private Integer triggerHostListid;

    private Integer triggerKwListid;

    private static final long serialVersionUID = 1L;

    public Integer getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(Integer triggerId) {
        this.triggerId = triggerId;
    }

    public Integer getTriggerHostListid() {
        return triggerHostListid;
    }

    public void setTriggerHostListid(Integer triggerHostListid) {
        this.triggerHostListid = triggerHostListid;
    }

    public Integer getTriggerKwListid() {
        return triggerKwListid;
    }

    public void setTriggerKwListid(Integer triggerKwListid) {
        this.triggerKwListid = triggerKwListid;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TriggerRelation other = (TriggerRelation) that;
        return (this.getTriggerId() == null ? other.getTriggerId() == null : this.getTriggerId().equals(other.getTriggerId()))
            && (this.getTriggerHostListid() == null ? other.getTriggerHostListid() == null : this.getTriggerHostListid().equals(other.getTriggerHostListid()))
            && (this.getTriggerKwListid() == null ? other.getTriggerKwListid() == null : this.getTriggerKwListid().equals(other.getTriggerKwListid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTriggerId() == null) ? 0 : getTriggerId().hashCode());
        result = prime * result + ((getTriggerHostListid() == null) ? 0 : getTriggerHostListid().hashCode());
        result = prime * result + ((getTriggerKwListid() == null) ? 0 : getTriggerKwListid().hashCode());
        return result;
    }
}
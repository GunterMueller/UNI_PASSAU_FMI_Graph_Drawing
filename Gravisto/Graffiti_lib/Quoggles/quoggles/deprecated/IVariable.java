package quoggles.deprecated;

/**
 *
 */
public interface IVariable {

    public void setValue(Object o);
    
    public Object getValue();
    
    public boolean isActive();
    
    public void setActive(boolean active);

}

package ru.ifmo.ctddev.tenishcev.compiler;

/**
 * Created by kris13 on 04.06.17.
 */
public class VariableContainer {
    public String pascalName;
    public String byteName;
    public String byteType;

    public VariableContainer(String pascalName, String fieldName, String type) {
        this.pascalName = pascalName;
        this.byteName = fieldName;
        this.byteType = type;
    }
}

package model;

public interface Identifiable<ID> {
    ID getID();
    void setID(ID id);
}
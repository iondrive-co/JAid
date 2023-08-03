package jaid;

import java.util.Objects;

public class NamedObject {

    private final String name;

    public NamedObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(name, ((NamedObject)o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}

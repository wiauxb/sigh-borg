package norswap.sigh.types;

public final class MatType extends Type
{
    public final Type componentType;

    public MatType (Type componentType) {
        this.componentType = componentType;
    }

    @Override public String name() {
        return "Mat#"+componentType.toString();
    }

    @Override public boolean equals (Object o) {
        return this == o ||
            o instanceof MatType && componentType.equals(((MatType)o).componentType)
            || o instanceof ArrayType && ((ArrayType) o).componentType instanceof ArrayType &&
            componentType.equals(((ArrayType)(((ArrayType) o).componentType)).componentType);
    }

    @Override public int hashCode () {
        return componentType.hashCode();
    }

    @Override
    public boolean isArrayLike () {
        return true;
    }
}

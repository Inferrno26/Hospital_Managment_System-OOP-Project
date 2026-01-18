package person;

import projectJava.Permission;
import java.util.Set;

public interface HasPermissions {
    Set<Permission> getPermissions();

    default boolean has(Permission p) {
        return getPermissions().contains(p);
    }
}

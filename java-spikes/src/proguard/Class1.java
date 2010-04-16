package proguard;

@SuppressWarnings("unused")
public class Class1
{
    public static void method_public_static() {}
    public void method_public()  {}
    private static void method_private_static() {}
    private void method_private() {}
    static void method_package_scope_static() {}
    void method_package_scope() {}
    
    public static void prefix_public_static() {}
    public void prefix_public()  {}
    private static void prefix_private_static() {}
    private void prefix_private() {}
    static void prefix_package_scope_static() {}
    void prefix_package_scope() {}
}

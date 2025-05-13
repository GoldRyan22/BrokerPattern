public class MathClient 
{
    public static void main(String[] args)
    {
        MathClientProxy proxy = new MathClientProxy();

        float a = 3.5f, b = 2.0f;

        float sum = proxy.add(a, b);
        float root = proxy.doSqrt(25.0f);

        System.out.println("Sum: " + sum);
        System.out.println("Sqrt: " + root);
    }
}

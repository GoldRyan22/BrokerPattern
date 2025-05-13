public class MathServerOrig implements MathServiceInterface 
{
    @Override
    public float add(float a, float b) {
        return a + b;
    }

    @Override
    public float doSqrt(float a) {
        return (float) Math.sqrt(a);
    }
}
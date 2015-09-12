package info.ottawaimagyar.katolikus.exporter;

public class ExporterMain
{
    public static void main(String[] args)
    {
        Exporter lExporter = new Exporter();
        try
        {
            lExporter.runWithArguments(args);
        } catch (Throwable e)
        {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

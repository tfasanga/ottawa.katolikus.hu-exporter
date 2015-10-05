package info.ottawaimagyar.katolikus.converter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

public class RemoveImgTags
{
    public static void main(String[] args)
    {
        RemoveImgTags lRemoveImgTags = new RemoveImgTags();
        try
        {
            lRemoveImgTags.runWithArguments(args);
        } catch (Throwable e)
        {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runWithArguments(String[] aInArgs) throws ParserConfigurationException, IOException, SAXException, XMLStreamException
    {
        XMLInputFactory lInputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory lOutputFactory = XMLOutputFactory.newInstance();
        XMLEventFactory lEventFactory = XMLEventFactory.newInstance();

        for(int i = 1; i <= 17; i++)
        {
            File lDir = new File("work");

            String lFileName = "posts_" + i + ".xml";
            File lFile = new File(lDir, lFileName);

            if(!lFile.exists())
            {
                throw new RuntimeException("Not found " + lFile.getCanonicalPath());
            }

            transformFile(lFile, lInputFactory, lOutputFactory, lEventFactory);
        }
    }

    private void transformFile(File aInFile, XMLInputFactory aInInputFactory, XMLOutputFactory aInOutputFactory, XMLEventFactory aInEventFactory)
            throws IOException, XMLStreamException
    {
        File lOutFile = new File(aInFile.getParent(), "out_" + aInFile.getName());

        FileInputStream lFileInputStream = null;
        FileOutputStream lFileOutputStream = null;

        try
        {
            lFileInputStream = new FileInputStream(aInFile);
            lFileOutputStream = new FileOutputStream(lOutFile);

            XMLStreamReader lStreamReader = aInInputFactory.createXMLStreamReader(lFileInputStream);
            XMLEventReader lEventReader = aInInputFactory.createXMLEventReader(lStreamReader);

            XMLEventWriter lEventWriter = aInOutputFactory.createXMLEventWriter(lFileOutputStream);

            boolean lIsContent = false;

            while (lEventReader.hasNext())
            {
                XMLEvent lEvent = lEventReader.nextEvent();
                XMLEvent lEventToWrite = lEvent;
                if (lEvent.isStartElement())
                {
                    StartElement lStartElement = lEvent.asStartElement();
                    QName lName = lStartElement.getName();
                    if (isContentEncodedTag(lName))
                    {
                        lIsContent = true;
                    }
                } else if (lEvent.isEndElement())
                {
                    EndElement lEndElement = lEvent.asEndElement();
                    QName lName = lEndElement.getName();
                    if (isContentEncodedTag(lName))
                    {
                        lIsContent = false;
                    }
                } else if (lIsContent)
                {
                    if (lEvent.isCharacters())
                    {
                        Characters lCharacters = lEvent.asCharacters();
                        String lData = lCharacters.getData();
                        String lNewData = transformPostHtml(lData);
                        lEventToWrite = aInEventFactory.createCData(lNewData);
                    }
                }

                lEventWriter.add(lEventToWrite);
            }
            lEventReader.close();
            lEventWriter.flush();
            lEventWriter.close();
        }
        finally
        {
            if(lFileOutputStream != null) lFileOutputStream.close();
            if(lFileInputStream != null) lFileInputStream.close();
        }
    }

    private String transformPostHtml(String aInData)
    {
        Document lContent = Jsoup.parse(aInData);
        Elements lImgElements = lContent.getElementsByTag("img");
        for (Element lImgElement : lImgElements)
        {
            Element lParent = lImgElement.parent();
            lImgElement.remove();
            if(lParent.tagName().equals("p"))
            {
                int i = lParent.childNodeSize();
                if(i == 0)
                {
                    lParent.remove();
                }
            }

        }

        Elements lBodies = lContent.getElementsByTag("body");
        Element lBody = lBodies.first();
        return lBody.html();
    }

    private boolean isContentEncodedTag(QName aInName)
    {
        boolean b = false;
        String lPrefix = aInName.getPrefix();
        if("content".equals(lPrefix))
        {
            String lLocalPart = aInName.getLocalPart();
            if(aInName.getLocalPart().equals("encoded"))
            {
                b = true;
            }
        }

        return b;
    }
}

/**************************************************************************
 * Author:             Tibor Fasanga
 * Description:
 * <p>
 * **************************************************************************
 * Source Control System Information
 * $Id: $
 * **************************************************************************
 * Copyright (c) 2012 Alcatel-Lucent.
 **************************************************************************/
package info.ottawaimagyar.katolikus.exporter;

import org.jsoup.nodes.Node;

public class Post
{
    private final String url;
    private Node dateStr;

    public Post(String aInUrl)
    {
        url = aInUrl;
    }

    @Override
    public String toString()
    {
        return url + ", " + dateStr;
    }

    public void setDateStr(Node aInDateStr)
    {
        dateStr = aInDateStr;
    }
}

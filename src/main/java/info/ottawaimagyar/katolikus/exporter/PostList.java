package info.ottawaimagyar.katolikus.exporter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class PostList
{
    private int bucketSize = 0;
    private LinkedList<List<Post>> bucketList;

    public PostList(int aInBucketSize)
    {
        bucketSize = aInBucketSize;
        bucketList = new LinkedList<>();
        bucketList.add(new ArrayList<>());
    }

    public void add(Post aInPost)
    {
        List<Post> lLast = bucketList.getLast();
        if(bucketSize > 0 && lLast.size() >= bucketSize)
        {
            lLast = new ArrayList<>();
            bucketList.add(lLast);
        }

        lLast.add(aInPost);
    }

    public List<List<Post>> getBucketList()
    {
        return bucketList;
    }

    public int size()
    {
        int lCount  = 0;
        for (List<Post> lPosts : bucketList)
        {
            lCount += lPosts.size();
        }
        return lCount;
    }
}

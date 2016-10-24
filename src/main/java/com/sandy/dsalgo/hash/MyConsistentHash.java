package com.sandy.dsalgo.hash;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyConsistentHash<T> {

    // # of times a bin is replicated in hash circle. (for better load balancing)
    private final int numberOfReplicas;

    private final SortedMap<BigInteger, T> circle = new TreeMap<BigInteger, T>();

    public MyConsistentHash(int numberOfReplicas, Collection<T> nodes) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            addBin(node);
        }
    }

    // Add a new bin to the consistent hash
    public void addBin(T bin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        for (int i = 0; i < numberOfReplicas; i++) {
            // The string addition forces each replica to have different hash
            circle.put(hash(bin.toString() + i), bin);
        }
    }

    public T getBinFor(Object key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (circle.isEmpty()) {
            return null;
        }
        BigInteger hash = hash(key.toString());
        T bin = circle.get(hash);

        if (bin == null) {
            // inexact match -- find the next value in the circle
            SortedMap<BigInteger, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            bin = circle.get(hash);
        }
        return bin;
    }

    // Generate hash
    private BigInteger hash(String node) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] checksum = md5.digest(node.getBytes("UTF-8"));
        return new BigInteger(1, checksum);
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.out.println("Consistent Hashing");
        MyConsistentHash consistentHash = new MyConsistentHash(2, new ArrayList());
        consistentHash.addBin("A");
        consistentHash.addBin("B");

        System.out.println("Bin for 10 : " + consistentHash.getBinFor(10));
        System.out.println("Bin for 20 : " + consistentHash.getBinFor(20));
        System.out.println("Bin for 30 : " + consistentHash.getBinFor(30));
        System.out.println("Bin for 40 : " + consistentHash.getBinFor(40));
        System.out.println("Bin for 10 : " + consistentHash.getBinFor(10));

        System.out.println("Done");
    }

}

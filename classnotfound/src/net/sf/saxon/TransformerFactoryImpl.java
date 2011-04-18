package net.sf.saxon;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;

import org.xml.sax.XMLFilter;

//Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
//Jad home page: http://www.geocities.com/kpdus/jad.html
//Decompiler options: packimports(3) 
//Source File Name:   TransformerFactoryImpl.java

public class TransformerFactoryImpl extends SAXTransformerFactory
{

 public TransformerFactoryImpl()
 {
 }

 public Transformer newTransformer(Source source)
     throws TransformerConfigurationException
 {
     Templates templates = newTemplates(source);
     Transformer transformer = templates.newTransformer();
     return transformer;
 }

 public Transformer newTransformer()
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }

 public Templates newTemplates(Source source)
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }

 public Source getAssociatedStylesheet(Source source, String s, String s1, String s2)
     throws TransformerConfigurationException
 {
         throw new UnsupportedOperationException();
 }

 private Source compositeStylesheet(String s, Source asource[])
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }

 public void setURIResolver(URIResolver uriresolver)
 {
     throw new UnsupportedOperationException();
 }

 public URIResolver getURIResolver()
 {
     throw new UnsupportedOperationException();
 }

 public boolean getFeature(String s)
 {
     throw new UnsupportedOperationException();
 }

 public void setAttribute(String s, Object obj)
     throws IllegalArgumentException
 {
     throw new UnsupportedOperationException();
 }

 public Object getAttribute(String s)
     throws IllegalArgumentException
 {
     throw new UnsupportedOperationException();
 }

 public void setErrorListener(ErrorListener errorlistener)
     throws IllegalArgumentException
 {
     throw new UnsupportedOperationException();
 }

 public ErrorListener getErrorListener()
 {
     throw new UnsupportedOperationException();
 }

 public TransformerHandler newTransformerHandler(Source source)
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }

 public TransformerHandler newTransformerHandler(Templates templates)
     throws TransformerConfigurationException
 {
         throw new UnsupportedOperationException();
 }

 public TransformerHandler newTransformerHandler()
     throws TransformerConfigurationException
 {
         throw new UnsupportedOperationException();
 }

 public TemplatesHandler newTemplatesHandler()
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }

 public XMLFilter newXMLFilter(Source source)
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }

 public XMLFilter newXMLFilter(Templates templates)
     throws TransformerConfigurationException
 {
         throw new UnsupportedOperationException();
 }

 public void setFeature(String s, boolean flag)
     throws TransformerConfigurationException
 {
     throw new UnsupportedOperationException();
 }
}

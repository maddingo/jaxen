/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * Copyright (C) 2000-2003 bob mcwhirter & James Strachan.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows 
 *    these conditions in the documentation and/or other materials 
 *    provided with the distribution.
 *
 * 3. The name "Jaxen" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact license@jaxen.org.
 * 
 * 4. Products derived from this software may not be called "Jaxen", nor
 *    may "Jaxen" appear in their name, without prior written permission
 *    from the Jaxen Project Management (pm@jaxen.org).
 * 
 * In addition, we request (but do not require) that you include in the 
 * end-user documentation provided with the redistribution and/or in the 
 * software itself an acknowledgement equivalent to the following:
 *     "This product includes software developed by the
 *      Jaxen Project (http://www.jaxen.org/)."
 * Alternatively, the acknowledgment may be graphical using the logos 
 * available at http://www.jaxen.org/
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE Jaxen AUTHORS OR THE PROJECT
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Jaxen Project and was originally 
 * created by bob mcwhirter <bob@werken.com> and 
 * James Strachan <jstrachan@apache.org>.  For more information on the 
 * Jaxen Project, please see <http://www.jaxen.org/>.
 * 
 * $Id$
 */


package org.jaxen.dom4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.helpers.XPathReaderFactory;

public class XPathTest extends TestCase
{

    private static final String BASIC_XML = "xml/basic.xml";

    public static void main( String[] args ) 
    {
        TestRunner.run( suite() );
    }
    
    public static Test suite() 
    {
        return new TestSuite( XPathTest.class );
    }
    
    public XPathTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        System.setProperty( XPathReaderFactory.DRIVER_PROPERTY,
                            "" );
    }

    public void tearDown()
    {

    }

    public void testConstruction()
    {
        try
        {
            new Dom4jXPath( "/foo/bar/baz" );
        }
        catch (SAXPathException e)
        {
            fail( e.getMessage() );
        }
    }

    public void testSelection()
    {
        try
        {
            XPath xpath = new Dom4jXPath( "/foo/bar/baz" );

            SAXReader reader = new SAXReader();

            Document doc = reader.read( BASIC_XML );

            List results = xpath.selectNodes( doc );

            assertEquals( 3,
                          results.size() );

            Iterator iter = results.iterator();

            assertEquals( "baz",
                          ((Element)iter.next()).getName() );

            assertEquals( "baz",
                          ((Element)iter.next()).getName() );

            assertEquals( "baz",
                          ((Element)iter.next()).getName() );

            assertTrue( ! iter.hasNext() );

        }
        catch (Exception e)
        {
            fail( e.getMessage() );
        }
    }
    
    public void testAsBoolean()
    {
        try
        {
            XPath xpath = new Dom4jXPath( "/root/a = 'a'" );

            SAXReader reader = new SAXReader();

            Document doc = reader.read( "xml/simple.xml" );

            boolean answer = xpath.booleanValueOf( doc );

            assertTrue( "Xpath worked: " + xpath, answer );

            xpath = new Dom4jXPath( "'a' = 'b'" );
            
            answer = xpath.booleanValueOf( doc );

            assertTrue( "XPath should return false: " + xpath, ! answer );

        }
        catch (Exception e)
        {
            fail( e.getMessage() );
        }
    }
    
    public void testJaxen20AttributeNamespaceNodes()
    {
        try
        {
            Namespace ns1 = Namespace.get("p1", "www.acme1.org");
            Namespace ns2 = Namespace.get("p2", "www.acme2.org");
            Element element = new DefaultElement("test", ns1);
            Attribute attribute = new DefaultAttribute("pre:foo", "bar", ns2);
            element.add(attribute); 
            Document doc = new DefaultDocument(element);
            
            XPath xpath = new Dom4jXPath( "//namespace::node()" );

            List results = xpath.selectNodes( doc );

            assertEquals( 3,
                          results.size() );

        }
        catch (Exception e)
        {
            fail( e.getMessage() );
        }
    }
    
    public void testNamespaceNodesAreInherited()
    {
        try
        {
            Namespace ns0 = Namespace.get("p0", "www.acme0.org");
            Namespace ns1 = Namespace.get("p1", "www.acme1.org");
            Namespace ns2 = Namespace.get("p2", "www.acme2.org");
            Element element = new DefaultElement("test", ns1);
            Attribute attribute = new DefaultAttribute("pre:foo", "bar", ns2);
            element.add(attribute);
            Element root = new DefaultElement("root", ns0);
            root.add(element);
            Document doc = new DefaultDocument(root);
            
            XPath xpath = new Dom4jXPath( "/*/*/namespace::node()" );

            List results = xpath.selectNodes( doc );

            assertEquals( 4,
                          results.size() );

        }
        catch (Exception e)
        {
            fail( e.getMessage() );
        }
    }
    
}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.11 at 06:37:51 PM CEST 
//


package eu.mapperproject.jmml.specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.mapper-project.eu/xmml}apply" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.mapper-project.eu/xmml}extra" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="from" use="required" type="{http://www.mapper-project.eu/xmml}instancePort" />
 *       &lt;attribute name="to" use="required" type="{http://www.mapper-project.eu/xmml}instancePort" />
 *       &lt;attribute name="size" type="{http://www.mapper-project.eu/xmml}unit" />
 *       &lt;attribute name="interactive" type="{http://www.mapper-project.eu/xmml}OptionalChoice" default="no" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "apply",
    "extra"
})
@XmlRootElement(name = "coupling")
public class Coupling {

    protected List<Apply> apply;
    protected List<AnyContent> extra;
    @XmlAttribute
    protected String name;
    @XmlAttribute(required = true)
    protected InstancePort from;
    @XmlAttribute(required = true)
    protected InstancePort to;
    @XmlAttribute
    protected Unit size;
    @XmlAttribute
    protected OptionalChoice interactive;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the apply property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the apply property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApply().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Apply }
     * 
     * 
     */
    public List<Apply> getApply() {
        if (apply == null) {
            apply = new ArrayList<Apply>();
        }
        return this.apply;
    }

    /**
     * Gets the value of the extra property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extra property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtra().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnyContent }
     * 
     * 
     */
    public List<AnyContent> getExtra() {
        if (extra == null) {
            extra = new ArrayList<AnyContent>();
        }
        return this.extra;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link InstancePort }
     *     
     */
    public InstancePort getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstancePort }
     *     
     */
    public void setFrom(InstancePort value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link InstancePort }
     *     
     */
    public InstancePort getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstancePort }
     *     
     */
    public void setTo(InstancePort value) {
        this.to = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Unit }
     *     
     */
    public Unit getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Unit }
     *     
     */
    public void setSize(Unit value) {
        this.size = value;
    }

    /**
     * Gets the value of the interactive property.
     * 
     * @return
     *     possible object is
     *     {@link OptionalChoice }
     *     
     */
    public OptionalChoice getInteractive() {
        if (interactive == null) {
            return OptionalChoice.NO;
        } else {
            return interactive;
        }
    }

    /**
     * Sets the value of the interactive property.
     * 
     * @param value
     *     allowed object is
     *     {@link OptionalChoice }
     *     
     */
    public void setInteractive(OptionalChoice value) {
        this.interactive = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}

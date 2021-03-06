package com.rackspace.com.papi.components.checker

import scala.reflect.BeanProperty

import com.rackspace.com.papi.components.checker.handler.ResultHandler
import com.rackspace.com.papi.components.checker.handler.ServletResultHandler

/**
 * This class contains all the configuration options for a {@link com.rackspace.com.papi.components.checker.Validator}
 *
 * A license is required for any SaxonEE functionality.  SaxonEE can be declared in <code>xslEngine</code>
 * and <code>xsdEngine</code>.
 */
class Config {
  //
  //  Setup appropriate factories.  We need these set to ensure config
  //  options work correctly.
  //
  System.setProperty ("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema/saxonica", "com.saxonica.jaxp.SchemaFactoryImpl")
  System.setProperty ("javax.xml.validation.SchemaFactory:http://www.w3.org/XML/XMLSchema/v1.1", "org.apache.xerces.jaxp.validation.XMLSchema11Factory")
  System.setProperty ("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema", "org.apache.xerces.jaxp.validation.XMLSchemaFactory")

  //
  //  Don't allow duplicate nodes in the machine.
  //
  @BeanProperty var removeDups : Boolean = true

  //
  //  Run code to validate that the validator was correctly generated
  //
  @BeanProperty var validateChecker : Boolean = true

  //
  //  The result handler, recives and manages all results.
  //
  @BeanProperty var resultHandler : ResultHandler = new ServletResultHandler


  //
  //  Use Xerces or SAXON-EE for XSD validation.  Note that the Saxon
  //  validator requires a license.
  //
  private var xsde : String = "Xerces"
  private val supportedXSDEngines = Set("Xerces", "SaxonEE")

  def xsdEngine : String = xsde
  def xsdEngine_= (engine : String) : Unit = {
    if (!supportedXSDEngines.contains(engine)) {
      throw new IllegalArgumentException("Unrecognized XSL engine: "+
        engine+" supported engines: "+supportedXSDEngines)
    }
    xsde = engine
  }

  def setXSDEngine (engine : String) : Unit = { xsdEngine_=(engine) }
  def getXSDEngine : String = xsdEngine


  //
  //  Use SAXON-EE for XSD validation: This means that in cases where
  //  XSD validation needs to be done use the Saxon XSD validator
  //  instead of the default Xerces validator.  Note that the Saxon
  //  validator requires a license.
  //
  @Deprecated
  def setUseSaxonEEValidation( use : Boolean ) : Unit = {

    depUseSaxonEEValidation = use

    if ( use ) {

      setXSLEngine( "SaxonEE" )
      setXSDEngine( "SaxonEE" )
    }
    else {

      setXSDEngine( "Xerces" )
    }
  }

  private var depUseSaxonEEValidation : Boolean = false

  @Deprecated
  def getUseSaxonEEValidation() : Unit = {

    xsdEngine == "SaxonEE"
  }

  @Deprecated
  def useSaxonEEValidation = getUseSaxonEEValidation()

  @Deprecated
  def useSaxonEEValidation_= ( use : Boolean ) : Unit = setUseSaxonEEValidation( use )

  //
  //  Check Well-Formed XML and JSON
  //
  @BeanProperty var checkWellFormed : Boolean = false

  //
  //  Check all XML against XSD Grammars
  //
  @BeanProperty var checkXSDGrammar : Boolean = false

  //
  //  Allow XSD grammar transform.  Transform the XML after
  //  validation, to fill in things like default values etc.
  //
  @BeanProperty var doXSDGrammarTransform : Boolean = false

  //
  //  Check all JSON against JSON Schema Grammars
  //
  @BeanProperty var checkJSONGrammar : Boolean = false

  //
  //  Ensure elemets are correct
  //
  @BeanProperty var checkElements : Boolean = false

  //
  //  XPath version used in the WADL.  Can be 1 or 2. If 1 is set the
  //  Xalan implementation will be used, if 2 then Saxon will be used.
  //  Note that XPath 2 with schema awareness requires a Saxon
  //  license.
  //
  private var xpv : Int = 1

  def xpathVersion : Int = xpv

  def xpathVersion_= (version : Int) : Unit = {
    if ((version != 1) && (version != 2))
      throw new IllegalArgumentException("XPath version can only be 1 or 2.")
    xpv = version
  }

  def setXPathVersion (version : Int) : Unit = { xpathVersion_=(version) }
  def getXPathVersion : Int = xpathVersion

  //
  //  Check plain parameters
  //
  @BeanProperty var checkPlainParams : Boolean = false

  //
  //  Enable preprocess extension
  //
  @BeanProperty var enablePreProcessExtension : Boolean = true

  //
  //  Enable ignore XSD extension
  //
  @BeanProperty var enableIgnoreXSDExtension : Boolean = true

  //
  //  Enable ignore JSON Schema extension
  //
  @BeanProperty var enableIgnoreJSONSchemaExtension : Boolean = true

  //
  //  Enable message extension
  //
  @BeanProperty var enableMessageExtension : Boolean = true

  //
  //  Enable rax-roles extension
  //
  @BeanProperty var enableRaxRolesExtension : Boolean = false

  //
  //  Mask rax-roles with 404 and 405 errors. By default rax-roles
  //  response with a 403 if there is a role mismatch, if
  //  maskRaxRoles403 is true then the respose will be 404 if no
  //  methods are accessible or 405 if some methods are available.
  //
  @BeanProperty var maskRaxRoles403 : Boolean = false

  //
  //  The XSL 1.0 engine to use.  Possible choices are Xalan, XalanC,
  //  and Saxon. Note that Saxon is an XSL 2.0 engine, but most 1.0
  //  XSLs should work fine.
  //
  private var xsle : String = "XalanC"
  private val supportedXSLEngines = Set("Xalan", "XalanC", "SaxonHE", "SaxonEE",
  "Saxon" )  // NOTE:  "Saxon" is deprecated as well, remove when removing depUseSaxonEEValidation

  def xslEngine : String = xsle
  def xslEngine_= (engine : String) : Unit = {

    if (!supportedXSLEngines.contains(engine)) {
      throw new IllegalArgumentException("Unrecognized XSL engine: "+
                                         engine+" supported engines: "+supportedXSLEngines)
    }

    xsle =  engine match {
      case "Saxon" => if (depUseSaxonEEValidation) "SaxonEE" else "SaxonHE"
      case _ => engine
    }
  }

  def setXSLEngine (engine : String) : Unit = { xslEngine_=(engine) }
  def getXSLEngine : String = xslEngine

  //
  //  This is an optimization where the well formness check and
  //  multiple XPath checks can be merged into a single check.
  //
  @BeanProperty var joinXPathChecks : Boolean = false

  //
  //  Check that required headers are set.
  //

  @BeanProperty var checkHeaders : Boolean = false

  //
  // Preserve the ability to process the request body always. Setting
  // this to true ensures that the request remains readable after
  // validation is performed. Setting this to true, however, may also
  // disable some optimizations.
  //

  @BeanProperty var preserveRequestBody : Boolean = false
}

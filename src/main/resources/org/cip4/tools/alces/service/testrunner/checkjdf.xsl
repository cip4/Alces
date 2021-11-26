<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tbl="urn:TheLanguageTable" exclude-result-prefixes="tbl"
    xmlns="http://www.w3.org/1999/xhtml">
    <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
        indent="yes" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>
    <!-- A variable to hold the current language, default to English -->
    <!-- Nederlandse vertaling door Koen Van de Poel, Agfa -->
    <!-- Extra warning/error counts added by Claes Buckwalter (but not translated yet) -->
    <xsl:param name="lang" select="//CheckOutput/@Language"/>
    <!-- here is the language table -->
    <tbl:lang>
        <tbl:loc key="title">
            <tbl:str lang="EN">CIP4 CheckJDF Output</tbl:str>
            <tbl:str lang="DE">CIP4 CheckJDF Output </tbl:str>
            <tbl:str lang="FR">CIP4 CheckJDF Output in French</tbl:str>
            <tbl:str lang="NL">CIP4 CheckJDF Output </tbl:str>
        </tbl:loc>
        <tbl:loc key="output">
            <tbl:str lang="EN">CheckJDF Validation Output</tbl:str>
            <tbl:str lang="DE">CheckJDF Validierungsoutput </tbl:str>
            <tbl:str lang="FR">CheckJDF Validation Output in French</tbl:str>
            <tbl:str lang="NL">CheckJDF Validatie Output</tbl:str>
        </tbl:loc>
        <tbl:loc key="CheckJDFVersion">
            <tbl:str lang="EN">CheckJDF Version: </tbl:str>
            <tbl:str lang="DE">CheckJDF Version: </tbl:str>
            <tbl:str lang="FR">CheckJDF Version: in French</tbl:str>
            <tbl:str lang="NL">CheckJDF Versie: </tbl:str>
        </tbl:loc>
        <tbl:loc key="version">
            <tbl:str lang="EN">Checker Version: </tbl:str>
            <tbl:str lang="DE">Checker Version: </tbl:str>
            <tbl:str lang="FR">Checker Version: in French</tbl:str>
            <tbl:str lang="NL">Checker Versie: </tbl:str>
        </tbl:loc>
        <tbl:loc key="TestFile">
            <tbl:str lang="EN">Testing File: </tbl:str>
            <tbl:str lang="DE">Überprüfung der Datei: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Testen van de File: </tbl:str>
        </tbl:loc>
        <tbl:loc key="PrivateElement">
            <tbl:str lang="EN">Private Element: </tbl:str>
            <tbl:str lang="DE">Privates Element: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Prive Element: </tbl:str>
        </tbl:loc>
        <tbl:loc key="PrivateContent">
            <tbl:str lang="EN">Private Element Content: </tbl:str>
            <tbl:str lang="DE">Privater ElementenInhalt: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Prive Element: Inhoud</tbl:str>
        </tbl:loc>
        <tbl:loc key="PrivateAttribute">
            <tbl:str lang="EN">Private Attribute: </tbl:str>
            <tbl:str lang="DE">Privates Attribut: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Prive Attribuut: </tbl:str>
        </tbl:loc>
        <tbl:loc key="at">
            <tbl:str lang="EN"> at: </tbl:str>
            <tbl:str lang="DE"> in: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL"> in: </tbl:str>
        </tbl:loc>
        <tbl:loc key="value">
            <tbl:str lang="EN"> value: </tbl:str>
            <tbl:str lang="DE"> Wert: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL"> waarde: </tbl:str>
        </tbl:loc>
        <tbl:loc key="UnknownElement">
            <tbl:str lang="EN"> Unknown Element: </tbl:str>
            <tbl:str lang="DE"> Unbekanntes Element: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL"> Onbekend Element: </tbl:str>
        </tbl:loc>
        <tbl:loc key="DeprecatedElement">
            <tbl:str lang="EN">Warning: Deprecated Element: </tbl:str>
            <tbl:str lang="DE"> Warnung: Veraltetes Element: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL"> Waarschuwing: oubollig Element:</tbl:str>
        </tbl:loc>
        <tbl:loc key="PrereleaseElement">
            <tbl:str lang="EN">Prerelease Element: </tbl:str>
            <tbl:str lang="DE">Zu neues Element: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Prerelease Element: </tbl:str>
        </tbl:loc>
        <tbl:loc key="SwapElement">
            <tbl:str lang="EN">Attribute written as Element: </tbl:str>
            <tbl:str lang="DE">Attribut wurde als Element geschrieben: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Attribuut als Element geschreven: </tbl:str>
        </tbl:loc>
        <tbl:loc key="MissLink">
            <tbl:str lang="EN">Missing ResourceLink: </tbl:str>
            <tbl:str lang="DE">Fehlender ResourceLink: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Missende ResourceLink: </tbl:str>
        </tbl:loc>
        <tbl:loc key="MissProcUsage">
            <tbl:str lang="EN">Missing ProcessUsage for ResourceLink: </tbl:str>
            <tbl:str lang="DE">Fehlende ProcessUsage für ResourceLink: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Missende ProcessUsage voor ResourceLink:
            </tbl:str>
        </tbl:loc>
        <tbl:loc key="InvalidLink">
            <tbl:str lang="EN">Invalid ResourceLink: </tbl:str>
            <tbl:str lang="DE">Unzul㲳iger ResourceLink: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Ongeldige ResourceLink</tbl:str>
        </tbl:loc>
        <tbl:loc key="MultiID">
            <tbl:str lang="EN"> refers to multiply defined ID: at: </tbl:str>
            <tbl:str lang="DE"> Bezieht sich auf mehrfach definierte ID: an: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL"> verwijst naar herhaaldelijk gedefinieerde ID:
                in: </tbl:str>
        </tbl:loc>
        <tbl:loc key="DangleLink">
            <tbl:str lang="EN">Dangling ResourceLink </tbl:str>
            <tbl:str lang="DE">Unverknüpfter ResourceLink </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Hangende (dangling) ResourceLink </tbl:str>
        </tbl:loc>
        <tbl:loc key="DanglePartLink">
            <tbl:str lang="EN">Dangling Partitioned ResourceLink </tbl:str>
            <tbl:str lang="DE">Unverknüpfter Partitionierter ResourceLink </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Hangende (dangling) gepartitioneerde
                ResourceLink: </tbl:str>
        </tbl:loc>
        <tbl:loc key="DangleRefElem">
            <tbl:str lang="EN">Dangling RefElement </tbl:str>
            <tbl:str lang="DE">Unverknüpfter RefElement</tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Hangend (dangling) RefElement </tbl:str>
        </tbl:loc>
        <tbl:loc key="DanglePartRefElem">
            <tbl:str lang="EN">Dangling Partitioned RefElement </tbl:str>
            <tbl:str lang="DE">Unverknüpfter Partitioniertes RefElement </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Hangend (dangling) gepartitioneerd RefElement:
            </tbl:str>
        </tbl:loc>
        <tbl:loc key="InvResPosition">
            <tbl:str lang="EN">Invalid position of Resource and ResourceLink </tbl:str>
            <tbl:str lang="DE">Unzulässige Position der Ressource und des
                RecourceLinks </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Ongeldige positie van Resource end ResourceLink:
            </tbl:str>
        </tbl:loc>
        <tbl:loc key="UnlinkedRes">
            <tbl:str lang="EN">Warning: Unlinked and Unreferenced Resource: </tbl:str>
            <tbl:str lang="DE">Warnung: Unverlinkte und Unreferenzierte
                Ressource: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Waarschuwing: Unlinked en Unreferenced Resource:
            </tbl:str>
        </tbl:loc>
        <tbl:loc key="InvRefElem">
            <tbl:str lang="EN">Invalid RefElement: </tbl:str>
            <tbl:str lang="DE">Unzulässiges RefElement: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Ongeldig RefElement: </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">General Invalid Element: </tbl:str>
            <tbl:str lang="DE">Allgemein unzulässiges Element: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Algemeen ongeldig Element: </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Separation not defined in ColorPool: </tbl:str>
            <tbl:str lang="DE">Separation im ColorPool nicht definiert: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Separatie niet gedefinieerd in ColorPool:
            </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Separation defined in ColorPool but never
                referenced: </tbl:str>
            <tbl:str lang="DE">Separation im ColorPool definiert aber nie
                referenziert: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Separatie gedefinieerd in ColorPool maar nooit
                gebruikt: </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Multiple ID Attribute at: </tbl:str>
            <tbl:str lang="DE">Mehrfaches ID Attribut vorhanden in: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Herhaald ID Attribuut in: </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Prerelease Attribute </tbl:str>
            <tbl:str lang="DE">Zu neues Attribut </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Prerelease Attribuut </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Deprecated Attribute </tbl:str>
            <tbl:str lang="DE">Veraltetes Attribut </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Oubollig Attribuut </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Unknown Attribute </tbl:str>
            <tbl:str lang="DE">Unbekanntes Attribut </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Onbekend Attribuut </tbl:str>
        </tbl:loc>
        <tbl:loc key="MissingAttribute">
            <tbl:str lang="EN">Missing Required Attribute </tbl:str>
            <tbl:str lang="DE">Fehlendes erforderliches Attribut </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Missend verplicht Attribuut </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Element written as Attribute: </tbl:str>
            <tbl:str lang="DE">Element als Attribut gechrieben: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Element geschreven als Attribuut: </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Tested Attribute </tbl:str>
            <tbl:str lang="DE">Getestetes Attribut </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Getest Attribuut </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Error Type: </tbl:str>
            <tbl:str lang="DE">Fehlertyp: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Fouttype: </tbl:str>
        </tbl:loc>
        <tbl:loc key="message">
            <tbl:str lang="EN">Message: </tbl:str>
            <tbl:str lang="DE">Nachricht: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Boodschap: </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Unmatched element: TODO fix xslt for this</tbl:str>
            <tbl:str lang="DE">Unangepasstes Element: Aufgabe xsl reparieren</tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Unmatched Element: TODO fix xslt
            hiervoor</tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Resource PartUsage: </tbl:str>
            <tbl:str lang="DE">Resource PartUsage: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Resource PartUsage: </tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaValidationSuccess">
            <tbl:str lang="EN">XML Schema Validation Successful</tbl:str>
            <tbl:str lang="DE">XML Validierung erfolgreich</tbl:str>
            <tbl:str lang="FR">XML Schema Validation Successful</tbl:str>
            <tbl:str lang="NL">XML Schema Validation geslaagd</tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaValidationNotPerformed">
            <tbl:str lang="EN">XML Schema Validation Not Performed</tbl:str>
            <tbl:str lang="DE">XML Schema Validation Not Performed</tbl:str>
            <tbl:str lang="FR">XML Schema Validation Not Performed</tbl:str>
            <tbl:str lang="NL">XML Schema Validation Not Performed</tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaValidationError">
            <tbl:str lang="EN">XML Schema Validation Error: </tbl:str>
            <tbl:str lang="DE">XML Validierungsfehler:</tbl:str>
            <tbl:str lang="FR">XML Schema Validation Error: in French </tbl:str>
            <tbl:str lang="NL">XML Schema Validation fout: </tbl:str>
        </tbl:loc>
        <tbl:loc key="prefix">
            <tbl:str lang="EN">XML Namespace prefix: </tbl:str>
            <tbl:str lang="DE">XML Namespace präfix: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">XML Namespace prefix: </tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaValidationOutput">
            <tbl:str lang="EN">XML Schema Validation Output</tbl:str>
            <tbl:str lang="DE">XML Schema Validierungs Output</tbl:str>
            <tbl:str lang="FR">XML Schema Validation Output</tbl:str>
            <tbl:str lang="NL">XML Schema Validatie Output</tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaErrors">
            <tbl:str lang="EN">Schema Errors: </tbl:str>
            <tbl:str lang="DE">Schema Errors: </tbl:str>
            <tbl:str lang="FR">Schema Errors: </tbl:str>
            <tbl:str lang="NL">Aantal Schema fouten: </tbl:str>
       </tbl:loc>
        <tbl:loc key="SchemaWarnings">
            <tbl:str lang="EN">Schema Warnings: </tbl:str>
            <tbl:str lang="DE">Schema Warnings: </tbl:str>
            <tbl:str lang="FR">Schema Warnings: </tbl:str>
            <tbl:str lang="NL">Aantal Schema waarschuwingen:</tbl:str>
       </tbl:loc>
        <tbl:loc key="CheckJDFErrors">
            <tbl:str lang="EN">CheckJDF Errors: </tbl:str>
            <tbl:str lang="DE">CheckJDF Errors: </tbl:str>
            <tbl:str lang="FR">CheckJDF Errors: </tbl:str>
           <tbl:str lang="NL">Aantal CheckJDF fouten: </tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaError">
            <tbl:str lang="EN">Schema Error: </tbl:str>
            <tbl:str lang="DE">Fehler im Schema: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Fout in Schema: </tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaFatalError">
            <tbl:str lang="EN">Schema Fatal Error: </tbl:str>
            <tbl:str lang="DE">Fataler Fehler im Schema: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Fatale fout in Schema: </tbl:str>
        </tbl:loc>
        <tbl:loc key="SchemaWarning">
            <tbl:str lang="EN">Schema Warning: </tbl:str>
            <tbl:str lang="DE">Schemawarnung: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Schema waarschuwing: </tbl:str>
        </tbl:loc>
        <tbl:loc key="CheckJDFValidationOutput">
            <tbl:str lang="EN">CheckJDF Validation Output</tbl:str>
            <tbl:str lang="DE">Validierungsoutput der Überprüfung</tbl:str>
            <tbl:str lang="FR">CheckJDF Validation Output</tbl:str>
            <tbl:str lang="NL">CheckJDF Validatie Output</tbl:str>
        </tbl:loc>
        <tbl:loc key="CheckJDFValidationSuccessful">
            <tbl:str lang="EN">CheckJDF Validation Successful</tbl:str>
            <tbl:str lang="DE">Überprüfung der JDF Validation war erfolgreich</tbl:str>
            <tbl:str lang="FR">CheckJDF Validation Successful</tbl:str>
            <tbl:str lang="NL">CheckJDF Validatie geslaagd</tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">XML Schema Validation Output: </tbl:str>
            <tbl:str lang="DE">Schema Validierungs Output:</tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">XML Schema Validatie Output: </tbl:str>
        </tbl:loc>
        <tbl:loc key="rawout">
            <tbl:str lang="EN">Raw XML Checker Output for: </tbl:str>
            <tbl:str lang="DE">CheckJDF Output als XML: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">CheckJDF Output als XML </tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Checker Version:</tbl:str>
            <tbl:str lang="DE">Checkerversion</tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Checker Versie:</tbl:str>
        </tbl:loc>
        <tbl:loc>
            <tbl:str lang="EN">Last Valid Version: </tbl:str>
            <tbl:str lang="DE">Letzte gültige Version: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Laatste geldige Versie:</tbl:str>
        </tbl:loc>
        <!-- KVDP added mssing First Valid Version -->
        <tbl:loc>
            <tbl:str lang="EN">First Valid Version: </tbl:str>
            <!-- KVDP translate for German -->
            <tbl:str lang="DE">Letzte gültige Version: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Eerste geldige Versie:</tbl:str>
        </tbl:loc>
        <tbl:loc key="DeviceCapTest">
            <tbl:str lang="EN">Device Capability Test </tbl:str>
            <!-- KVDP corrected German string -->
            <tbl:str lang="DE">Test der Gerätefähigkeiten: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Device Capability Test </tbl:str>
        </tbl:loc>
        <tbl:loc key="ExecutableNodes">
            <tbl:str lang="EN">Executable node list</tbl:str>
            <tbl:str lang="DE">Liste der Ausführbaren Knoten: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Lijst met uitvoerbare nodes</tbl:str>
        </tbl:loc>
        <tbl:loc key="RejectedNode">
            <tbl:str lang="EN">Rejected node</tbl:str>
            <tbl:str lang="DE">Verworfener Knoten: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Geweigerde node</tbl:str>
        </tbl:loc>
        <tbl:loc key="BugReport">
            <tbl:str lang="EN">Bug Report</tbl:str>
            <tbl:str lang="DE">Fehlerbericht </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Foutbericht</tbl:str>
        </tbl:loc>
        <tbl:loc key="CapsType">
            <!-- KVDP fixed error in English and German string-->
            <tbl:str lang="EN">Valid Type in Capabilities: </tbl:str>
            <tbl:str lang="DE">Erlaubte Typen in den Gerätefähigkeiten: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Toegelaten Type in Capabilties: </tbl:str>
        </tbl:loc>
        <tbl:loc key="MissingElement">
            <tbl:str lang="EN">Missing Sub-Element: </tbl:str>
            <tbl:str lang="DE">Fehlendes Subelement: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Missend Sub-Element: </tbl:str>
        </tbl:loc>
        <tbl:loc key="MinOccurs">
            <tbl:str lang="EN">Minimum number of occurrences: </tbl:str>
            <tbl:str lang="DE">Minimale Anzahl: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Minimaal aantal: </tbl:str>
        </tbl:loc>
        <tbl:loc key="MaxOccurs">
            <tbl:str lang="EN">Maximum number of occurrences: </tbl:str>
            <tbl:str lang="DE">Maximale Anzahl: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Maximaal aantal: </tbl:str>
        </tbl:loc>
        <tbl:loc key="Occurs">
            <tbl:str lang="EN">Occurrences: </tbl:str>
            <tbl:str lang="DE">Anzahl: </tbl:str>
            <tbl:str lang="FR">Testing File: in French </tbl:str>
            <tbl:str lang="NL">Aantal: </tbl:str>
        </tbl:loc>
    </tbl:lang>
    <!-- get a pointer to the language table -->
    <xsl:variable name="table" select="document('')/xsl:stylesheet/tbl:lang"/>
    <!-- a named template to do the gory localization stuff -->
    <xsl:template name="localize">
        <xsl:param name="string" select="''"/>
        <!--TOD move all definitions to the key method -->
        <!-- find the appropriate localizations for the key -->
        <xsl:variable name="loc1" select="$table/tbl:loc[@key=$string]"/>
        <!-- find the appropriate localizations for the English string -->
        <xsl:variable name="loc"
            select="$table/tbl:loc[tbl:str/@lang='EN' and tbl:str=$string]"/>
        <!-- get the localized string -->
        <xsl:value-of select="$loc/tbl:str[@lang=$lang]"/>
        <xsl:value-of select="$loc1/tbl:str[@lang=$lang]"/>
    </xsl:template>
    <!-- here are the processing templates -->
    <xsl:template match="CheckOutput">
        <!-- Count validation errors -->
        <xsl:variable name="schemaErrors">
            <xsl:choose>
                <xsl:when
                    test="TestFile/SchemaValidationOutput[@ValidationResult='NotPerformed']"
                    >-</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                        select="count(TestFile/SchemaValidationOutput/Error) + count(TestFile/SchemaValidationOutput/FatalError)"
                    />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="schemaWarnings">
            <xsl:choose>
                <xsl:when
                    test="TestFile/SchemaValidationOutput[@ValidationResult='NotPerformed']"
                    >-</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                        select="count(TestFile/SchemaValidationOutput/Warning)"
                    />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="checkjdfErrors">
            <xsl:value-of
                select="count(//TestFile/CheckJDFOutput//*[@IsValid='false' and not(*)])"
            />
        </xsl:variable>
        <!-- XHTML -->
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <title>
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'title'"/>
                    </xsl:call-template>
                </title>
                <style type="text/css" media="all"> 
                <xsl:comment>
                body {
				 margin-top: 0.5em;
				 margin-left: 0.5em;
				 font-family: verdana, sans-serif;
				 font-size: small;
				 background-color: white;
				}
				
				strong {
				 font-weight: bold;
				}
				
				.schema {
				 border-bottom-style: solid;
				 border-top-style: solid;
				 border-color: #CCCCCC;
				 border-width: 1px;
				 padding-bottom: 0.5em;
				}
				
				.checkjdf {
				 border-bottom-style: solid;
				 border-color: #CCCCCC;
				 border-width: 1px;
				 padding-bottom: 0.5em;
				}
				
				.valid {
				 color: #00ff00;
				}
				
				.invalid {
				 color: #ff0000;
				}
				
				.jdf {
				 font-weight: bold;
				}
				
				h1 {
				 margin-top: 0;
				}
				
				h2,h3 {
				 font-weight: bold;
				 margin-bottom: 0.5em;
				}
				
				h4,h5 {
				 font-size: small;
				 font-weight: normal;
				 margin-top: 0;
				 margin-bottom: 0;
				}
				
				ul {
				 margin-left: 1em;
				 margin-top: 0;
				}
				
				li {
				 margin-bottom: 0.5em;
				}
				
				code {
				 font-family: monospace;
				/* font-weight: bold; */
				 color: #000000;
				 padding: 2px;
				 background-color: #CCCCCC;
				}
				</xsl:comment>
                </style>
                
                <!-- 
                <link rel="stylesheet" type="text/css" media="all"
                    href="../conf/report/checkjdf.css"/>
                     -->
            </head>
            <body bgcolor="#CCFFFF">
                <xsl:comment>#include
                    virtual="/global/navigation/menue_switch.php?section=support" </xsl:comment>
                <h1>
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'output'"/>
                    </xsl:call-template>
                </h1>
                <p>
                    <!--<strong>Tested File: </strong> <xsl:value-of select="@XMLFile"/><br/>-->

                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'SchemaErrors'"/>
                    </xsl:call-template>
                    <strong>
                        <xsl:choose>
                            <xsl:when test="$schemaErrors=0">
                                <span class="valid">
                                    <xsl:value-of select="$schemaErrors"/>
                                </span>
                            </xsl:when>
                            <xsl:when test="$schemaErrors>0">
                                <span class="invalid">
                                    <xsl:value-of select="$schemaErrors"/>
                                </span>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$schemaErrors"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </strong>
                    <br/>

                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'SchemaWarnings'"
                        />
                    </xsl:call-template>
                    <strong>
                        <xsl:choose>
                            <xsl:when test="$schemaWarnings=0">
                                <span class="valid">
                                    <xsl:value-of select="$schemaWarnings"/>
                                </span>
                            </xsl:when>
                            <xsl:when test="$schemaWarnings>0">
                                <span class="invalid">
                                    <xsl:value-of select="$schemaWarnings"/>
                                </span>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$schemaWarnings"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </strong>
                    <br/>

                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'CheckJDFErrors'"
                        />
                    </xsl:call-template>
                    <strong>
                        <xsl:choose>
                            <xsl:when test="$checkjdfErrors=0">
                                <span class="valid">
                                    <xsl:value-of select="$checkjdfErrors"/>
                                </span>
                            </xsl:when>
                            <xsl:when test="$checkjdfErrors>0">
                                <span class="invalid">
                                    <xsl:value-of select="$checkjdfErrors"/>
                                </span>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$schemaWarnings"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </strong>
                    <br/>
                    <!--<xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'CheckJDFVersion'"
                        />
                    </xsl:call-template>

                    <xsl:value-of select="@Version"/>
                    <br/>-->
                    <!--
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'version'"/>
                    </xsl:call-template>
                    <xsl:value-of select="@Version"/>
                    <br/>
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'rawout'"/>
                    </xsl:call-template>
                    <br/>
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                            <xsl:value-of select="@XMLUrl"/>
                        </xsl:attribute>
                        <xsl:value-of select="@XMLFile"/>
                    </xsl:element>
                    -->
                </p>
                <xsl:apply-templates/>
                <xsl:comment>#include
                virtual="/global/bottom_short.php"</xsl:comment>
            </body>
        </html>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestFile">
        <!--        <h3>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'TestFile'"/>
            </xsl:call-template>
            <xsl:value-of select="@FileName"/>
        </h3>-->
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="SchemaValidationOutput[@ValidationResult='Valid']">
        <div class="schema">
            <h3 class="valid">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'SchemaValidationSuccess'"/>
                </xsl:call-template>
            </h3>
        </div>
    </xsl:template>

    <xsl:template
        match="SchemaValidationOutput[@ValidationResult='NotPerformed']">
        <div class="schema">
            <h3>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'SchemaValidationNotPerformed'"/>
                </xsl:call-template>
            </h3>
        </div>
    </xsl:template>

    <!-- =============================================== -->
    <xsl:template match="SchemaValidationOutput">
        <div class="schema">
            <h3 class="invalid">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'SchemaValidationOutput'"/>
                </xsl:call-template>
            </h3>
            <ul>
                <xsl:apply-templates/>
            </ul>
        </div>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="SchemaValidationOutput/Error">
        <li>
            <span style="color: #ff3333">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'SchemaValidationError'"/>
                </xsl:call-template>
            </span>
            <xsl:value-of select="@Message"/>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="SchemaValidationOutput/FatalError">
        <li>
            <span style="color: #ff3333">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'SchemaFatalError'"/>
                </xsl:call-template>
            </span>
            <xsl:value-of select="@Message"/>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="SchemaValidationOutput/Warning">
        <li>
            <span style="color: #aaaa00">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'SchemaWarning'"/>
                </xsl:call-template>
            </span>
            <xsl:value-of select="@Message"/>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="CheckJDFOutput[@IsValid='false']">
        <div class="checkjdf">
            <h3 class="invalid">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'CheckJDFValidationOutput'"/>
                </xsl:call-template>
            </h3>
            <ul>
                <xsl:apply-templates/>
            </ul>
        </div>
    </xsl:template>

    <xsl:template match="CheckJDFOutput">
        <div class="checkjdf">
            <h3 class="valid">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'CheckJDFValidationSuccessful'"/>
                </xsl:call-template>
            </h3>
        </div>
    </xsl:template>
    <!-- =============================================== -->
    <!--
    <xsl:template match="CheckJDFOutput/TestElement[@IsValid='false']">       
        <h3 class="invalid">
            <xsl:call-template name="localize">
                <xsl:with-param name="string"
                    select="'CheckJDFValidationOutput'"/>
            </xsl:call-template>
        </h3>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='InvalidElement']">
        <!-- KVDP comment end was missing? -->
        <!-- =============================================== 
        <h4>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="Invalid Element: ''"/>
            </xsl:call-template>
            <xsl:value-of select="@NodeName"/>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'at'"/>
            </xsl:call-template>
            <code><xsl:value-of select="@XPath"/></code>
        </h4>
        <xsl:text>Message: </xsl:text>
        <xsl:value-of select="@Message"/>
 =============================================== -->
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='PrivateElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'PrivateElement'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='MissingElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Missing Required Element: '"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='UnknownElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'UnknownElement'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='DeprecatedElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'DeprecatedElement'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='PrereleaseElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'PrereleaseElement'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='SwapElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'SwapElement'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='MissingResourceLink']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'MissLink'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='UnknownResourceLink']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Missing ResourceLink: '"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='MissingProcessUsage']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'MissProcUsage'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='InvalidResourceLink']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'InvalidLink'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='ResLinkMultipleID']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'ResourceLink '"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'MultiID'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='DanglingResLink']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'DangleLink'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='DanglingPartResLink']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'DanglePartLink'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
                <br/> Resource PartUsage: <xsl:value-of
                    select="@ResourcePartUsage"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='DanglingRefElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'DangleRefElem'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='DanglingPartRefElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'DanglePartRefElem'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
                <br/> Resource PartUsage: <xsl:value-of
                    select="@ResourcePartUsage"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='InvalidPosition']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'InvResPosition'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='UnlinkedResource']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'UnlinkedRes'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='InvalidRefElement']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'InvRefElem'"/>
                </xsl:call-template>
                <xsl:value-of select="@NodeName"/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestElement[@ErrorType='PrivateContents']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'PrivateContents'"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h4>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <!-- display partition keys -->
    <xsl:template match="TestElement/Part">
        <xsl:for-each select="@*">
            <br/>Partition key: <xsl:value-of select="name()"/> = <xsl:value-of
                select="."/>
        </xsl:for-each>
    </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="DeviceCapTest">
        <!--<hr/>-->
        <h3>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'DeviceCapTest'"/>
            </xsl:call-template>
        </h3> Execution time: <xsl:value-of select="@DeviceCapTestTime"/>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="ExecutableNodes">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'ExecutableNodes'"/>
                </xsl:call-template>
            </h4>
            <xsl:apply-templates/>
        </li>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="BugReport">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'BugReport'"/>
                </xsl:call-template>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="RejectedNode">
        <h3>
            <font>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'RejectedNode'"/>
                </xsl:call-template>
            </font>
        </h3>
        <xsl:call-template name="localize">
            <xsl:with-param name="string" select="'CapsType'"/>
        </xsl:call-template>
        <xsl:value-of select="@CapsType"/>
        <br/>
        <xsl:value-of select="@NodeType"/>
        <xsl:call-template name="localize">
            <xsl:with-param name="string" select="'at'"/>
        </xsl:call-template>
        <code>
            <xsl:value-of select="@XPath"/>
        </code> ; ID= <xsl:value-of select="@ID"/>
        <br/>
        <xsl:call-template name="localize">
            <xsl:with-param name="string" select="'message'"/>
        </xsl:call-template>
        <xsl:value-of select="@Message"/>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="RejectedChildNode">
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="MissingElement">
        <li>
            <h4>
                <font>
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string" select="'MissingElement'"
                        />
                    </xsl:call-template>
                    <xsl:value-of select="@Name"/>
                </font>
            </h4> # Occurrences: <xsl:value-of select="@FoundElements"/>;
                <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'MinOccurs'"/>
            </xsl:call-template>
            <xsl:value-of select="@MinOccurs"/>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'at'"/>
            </xsl:call-template>
            <code>
                <xsl:value-of select="@XPath"/>
            </code>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="MissingAttribute">
        <li>
            <h4>
                <font>
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string"
                            select="'MissingAttribute'"/>
                    </xsl:call-template>
                    <xsl:value-of select="@Name"/>
                </font>
            </h4>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'at'"/>
            </xsl:call-template>
            <code>
                <xsl:value-of select="@XPath"/>
            </code>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="Evaluation">
        <li>
            <h4>
                <font>
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string"
                            select="'MissingAttribute'"/>
                    </xsl:call-template>
                    <xsl:value-of select="@Name"/>
                </font>
            </h4>
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'at'"/>
            </xsl:call-template>
            <code>
                <xsl:value-of select="@XPath"/>
            </code>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestFile/Error">
        <h3 style="color: #ff0000">
            <xsl:call-template name="localize">
                <xsl:with-param name="string" select="'General Error: '"/>
            </xsl:call-template>
        </h3>
        <xsl:call-template name="localize">
            <xsl:with-param name="string" select="'Error Type: '"/>
        </xsl:call-template>
        <xsl:value-of select="@ErrorType"/>
        <br/>
        <xsl:call-template name="localize">
            <xsl:with-param name="string" select="'message'"/>
        </xsl:call-template>
        <xsl:value-of select="@Message"/>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="SeparationPool">
        <h3>
            <font color="#000000">
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Warning: Inconsistent Separations: '"/>
                </xsl:call-template>
            </font>
        </h3>
        <li>
            <xsl:apply-templates/>
        </li>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="SeparationPool/Warning[@ErrorType='MissingSeparation']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Separation not defined in ColorPool: '"/>
                </xsl:call-template>
                <xsl:value-of select="@Separation"/>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template
        match="SeparationPool/Warning[@ErrorType='UnreferencedSeparation']">
        <li>
            <h4>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Separation defined in ColorPool but never referenced: '"
                    />
                </xsl:call-template>
                <xsl:value-of select="@Separation"/>
            </h4>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- Attributes below here -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='MultipleID']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Multiple ID Attribute at: '"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
            <div>
                <xsl:text>ID: </xsl:text>
                <xsl:value-of select="@Value"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='PreReleaseAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Prerelease Attribute '"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span> at:<code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
            <div>
                <!-- KVDP first valid version was not translated -->
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'First Valid Version: '"/>
                </xsl:call-template>
                <xsl:value-of select="@FirstVersion"/>
                <!-- KVDP added break as for last version -->
                <br/>
                <xsl:call-template name="localize">

                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='DeprecatedAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Deprecated Attribute '"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Last Valid Version: '"/>
                </xsl:call-template>
                <xsl:value-of select="@LastVersion"/>
                <br/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='PrivateAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'PrivateAttribute'"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'prefix'"/>
                </xsl:call-template>
                <xsl:value-of select="@NSPrefix"/>
                <br/>
                <xsl:text>Namespace URI: </xsl:text>
                <xsl:value-of select="@NSURI"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='UnknownAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'Unknown Attribute '"
                    />
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='MissingAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'MissingAttribute'"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='InvalidAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Invalid Attribute Value '"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <!-- KVDP translated at -->
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>: <xsl:value-of select="@Value"
                />
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute[@ErrorType='SwapAttribute']">
        <li>
            <h5>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string"
                        select="'Element written as Attribute: '"/>
                </xsl:call-template>
                <span class="jdf">
                    <xsl:value-of select="@NodeName"/>
                </span>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'value'"/>
                </xsl:call-template>
                <xsl:value-of select="@Value"/>
            </h5>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="TestAttribute">
        <li>
            <h5>
                <font color="#ff0000">
                    <xsl:call-template name="localize">
                        <xsl:with-param name="string"
                            select="'Tested Attribute'"/>
                    </xsl:call-template>
                    <span class="jdf">
                        <xsl:value-of select="@NodeName"/>
                    </span>
                </font>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'at'"/>
                </xsl:call-template>
                <code>
                    <xsl:value-of select="@XPath"/>
                </code>
            </h5>
            <div>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'Error Type: '"/>
                </xsl:call-template>
                <xsl:value-of select="@ErrorType"/>
                <br/>
                <xsl:call-template name="localize">
                    <xsl:with-param name="string" select="'message'"/>
                </xsl:call-template>
                <xsl:value-of select="@Message"/>
            </div>
        </li>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="not"> &lt; not &gt; = <xsl:value-of
            select="@Value"/> [ <br/>
        <xsl:apply-templates/>
        <br/>] <br/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="or"> &lt; or &gt; = <xsl:value-of
            select="@Value"/> [ <br/>
        <xsl:apply-templates/>
        <br/>] </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="and"> &lt; and &gt; = <xsl:value-of
            select="@Value"/>
        <br/> [ <br/>
        <xsl:apply-templates/>
        <br/>] </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="TestElement">
        <!--  dummy to remove duplicate invalid elements -->
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <xsl:template match="InvalidResources">
        <!--  dummy to remove duplicate invalid elements -->
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="MissingAttributes">
        <!--  dummy to remove lists -->
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="ActionReportList">
        <!--  dummy to remove lists -->
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <!-- =============================================== -->
    <xsl:template match="*">
        <h4 style="color: #ff3333">

            <xsl:call-template name="localize">
                <xsl:with-param name="string"
                    select="'Unmatched element: TODO fix xslt for this'"/>
            </xsl:call-template>
        </h4>
        <div>
            <xsl:value-of select="name()"/>
        </div>
        <xsl:apply-templates/>
    </xsl:template>
    <!-- =============================================== -->
</xsl:stylesheet>

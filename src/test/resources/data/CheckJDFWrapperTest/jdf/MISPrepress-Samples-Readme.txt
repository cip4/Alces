MISPrepressICS-Samples 1.17
2006-02-07

These samples (should) comply with the current Base, MIS and MISPRE and PRECP ICS as published.

* Goal of test files:
- verification of the samples and ICS documents
- prepress systems can verify if they can consume and process these files
- MIS systems can verify if they could produce a similar file

Post your comments to the O&P forum and directly into the Samples Comments

* Validation
- Passes the CheckJDF 74 version (January 2006) without problems except for the schema complaint on missing URL (because schema does not check Incomplete Status)
- Passes JDFEditor build28d

Changes that were made compared to previous versions are listed in the header of each file.

* Main Samples description
- MISPrepress-ICS-Minimal.jdf (v11): a minimal JDF 1.2 job using gray boxes. All optional parameters and attributes were removed, no JMF (could be out of sync with others).
- MISPrepress-ICS-PlateMaking.jdf (v15) : the same job but with more parameters (optional). A JMF subscription is included.
- MISPrepress-ICS-ImpoProof_PlateMaking.jdf (v19): the same job as before but with a separate proofing node. After this imposition proof is approved, the platemaking can start.
- MISPrepress-ICS-Complex.jdf (v22): multiple prepress parts, more colors. It also has parts for ConventionalPrinting.
- Proofing folder: added combined Proof&Platemaking example

The PDF files that go with these samples are in the 'Contents' folder which must next to the JDF file (relative path).

- MISPrepress-ICS-PlateMaking.mjd: PlateMaking jdf and content PDF combined in a mime file

* A quick list of changes applied to most files since 1.16 release:
- add ProductID for proofs to in ProofxPlatemaking
- add ProductID for plates in Complex sample
JMF
- moved out of Extra folder
- added Exact=true for resource signals and commands in JMF folder

Proofing
- added MISPrepress-ICS-ImpoProofPlateMaking.jdf (ProofPlateMaking gray box)

Content
- Complexxxx.pdf: PDF files with proper colors for Cover, Body and Insert
- PlateMakingBrochure.pdf: PDF file for brochure sample

Proofing
New folder that has various variations described in the Proofing addendum ICS (hard copy <> softproofing'; page<>imposition proofing; with and without explicit approval)


* Extras folder changes
= JMF messages: updated, variations added

* Summary of Extras folder files
Note: except for the content files, these support files were delivered by different vendors with different versions of the sample. So slight differences are possible (marks etc.)


ScreenShots
- ComplexStripxxx.png: graphical dumps of Stripping, created with TrueFlow, courtesy of Dainippon Screen.
- PlateMakingStrip.png: graphical dumps of Stripping, created with TrueFlow, courtesy of Dainippon Screen.
- xxSheetxx: PDF outputs of the Complex sample, created with BackStage, courtesy of Esko-Graphics
- ConvPrint.jpg: graphical dump of the Conventional Printing part, courtesy of Man-Roland

InkPreviews
- xxx.png: low resolution preview files for each sheet/side/separation, created with PrintReady, courtesy of Heidelberg. Currently only for complex sample.

JMF
Messages (ResourceCommands and ResourceSignals) as send out by a prepress device to a press controller 

prepared by Koen Van de Poel, Agfa

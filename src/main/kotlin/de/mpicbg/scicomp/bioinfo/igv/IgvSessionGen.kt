package de.mpicbg.scicomp.bioinfo.igv


/**
 * Support API to programmatically create an IGV session file.
 */


import java.io.File

// todo optionally call igvtools to build appropriate indicies
// todo move to dependency library for clarity
// todo support genome file as input instead of fasta (see http://www.broadinstitute.org/igv/LoadGenome)
// todo validate that session can be loaded at the end using programmatic interface of iGV

// todo add option to build region set from existing bedfile to stream-line region navigation (or simply import bed file directly within IGV)

interface IGVTrack {
    fun toTrackXML(): String
    fun getResourceFile(): File
}

class BamTrack(val bamFile: File, val isCollapsed: Boolean = true) : IGVTrack {

    constructor(bamFile: String) : this(File(bamFile))

    override fun getResourceFile(): File = bamFile

    override fun toTrackXML() = """        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="${bamFile.absolutePath}" name="${bamFile.name.removeSuffix(".bam")}" sortable="true" visible="true">
            <RenderOptions colorByTag="" colorOption="READ_STRAND" flagUnmappedPairs="false" groupByTag="" maxInsertSize="1000" minInsertSize="50" shadeBasesOption="QUALITY" shadeCenters="true" showAllBases="false" sortByTag=""/>
        </Track>"""
}


class VcfTrack(val vcfFile: File) : IGVTrack {
    constructor(vcfFile: String) : this(File(vcfFile))

    override fun getResourceFile(): File = vcfFile

    override fun toTrackXML(): String = """        <Track SQUISHED_ROW_HEIGHT="4" altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" colorMode="GENOTYPE" displayMode="EXPANDED" featureVisibilityWindow="1998000" fontSize="10" id="${vcfFile.absolutePath}" name="${vcfFile.name.removeSuffix(".gz").removeSuffix("vcf")}" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>"""
}

class BedTrack(val bedFile: File) : IGVTrack {
    constructor(bedFile: String) : this(File(bedFile))

    override fun getResourceFile(): File = bedFile

    override fun toTrackXML(): String = """        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="1000000" fontSize="10" id="${bedFile.absolutePath}" name="${bedFile.name.removeSuffix(".gz").removeSuffix(".bed")}" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>
"""
}

class BigWigTrack(val bwFile: File) : IGVTrack {
    constructor(bwFile: String) : this(File(bwFile))

    override fun getResourceFile(): File = bwFile

    override fun toTrackXML(): String = """                <Track altColor="0,0,178" autoScale="true" clazz="org.broad.igv.track.DataSourceTrack" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="${bwFile.absolutePath}" name="${bwFile.name.removeSuffix(".bw")}" normalize="false" renderer="BAR_CHART" sortable="true" visible="true" windowFunction="mean">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="960.5058" minimum="0.0" type="LINEAR"/>
        </Track>

    """
}

fun builSession(genome: String, tracks: List<IGVTrack>): String {

    val resourceXML = tracks.map { it.getResourceFile() }.map { """<Resource path="${it}"/>""" }.joinToString("\n")
    val trackXML = tracks.map { it.toTrackXML() }.joinToString("\n")

    return """
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Session genome="${if (File(genome).exists()) File(genome).absolutePath else genome}" hasGeneTrack="false" hasSequenceTrack="true" path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/igv_session_ratio_difference.xml" version="8">
    <Resources>
       ${resourceXML}
    </Resources>
    <Panel height="962" name="DataPanel" width="2504">
        ${trackXML}
    </Panel>
    <Panel height="137" name="FeaturePanel" width="2504">
        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="Reference sequence" name="Reference sequence" sortable="false" visible="true"/>
    </Panel>
    <PanelLayout dividerFractions="0.8716094032549728"/>
    <HiddenAttributes>
        <Attribute name="DATA FILE"/>
        <Attribute name="DATA TYPE"/>
        <Attribute name="NAME"/>
    </HiddenAttributes>
</Session>
""".trim()

}

fun guessTracks(vararg trackFiles: String) = guessTracks(trackFiles.map { File(it) })

fun guessTracks(trackFiles: List<File>) = trackFiles.map {
    when {
        it.name.endsWith(".bed") || it.name.endsWith(".bed.gz") -> BedTrack(it)
        it.name.endsWith(".vcf") || it.name.endsWith(".vcf.gz") -> VcfTrack(it)
        it.name.endsWith(".bam") -> BamTrack(it)
        it.name.endsWith(".bw") -> VcfTrack(it)
        else -> throw IllegalArgumentException("unsupported track type")
    }
}

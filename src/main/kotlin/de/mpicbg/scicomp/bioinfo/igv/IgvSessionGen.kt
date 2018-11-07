package de.mpicbg.scicomp.bioinfo.igv


/**
 * Support API to programmatically create an IGV session file.
 */


import de.mpicbg.scicomp.kutils.stopIfNot
import java.io.File

// todo optionally call igvtools to build appropriate indicies
// todo move to dependency library for clarity
// todo support genome file as input instead of fasta (see http://www.broadinstitute.org/igv/LoadGenome)
// todo validate that session can be loaded at the end using programmatic interface of iGV
// todo migrate to use https://github.com/redundent/kotlin-xml-builder

// todo add option to build region set from existing bedfile to stream-line region navigation (or simply import bed file directly within IGV)


enum class Panel { Upper, Lower }

enum class DisplayMode { Expanded, Collapsed;

    override fun toString(): String {
        return super.toString().toUpperCase()
    }
}

abstract class IGVTrack {
    var panel = Panel.Upper
    var displayMode = DisplayMode.Collapsed
    abstract fun toTrackXML(): String
    abstract fun getResourceFile(): File
}

class BamTrack(val bamFile: File, val isCollapsed: Boolean = true) : IGVTrack() {

    constructor(bamFile: String) : this(File(bamFile))

    init {
        stopIfNot(bamFile.exists()) { "Track file '${bamFile}' does not exist" }
        stopIfNot(File(bamFile.absolutePath + ".bai").exists()) { "index file is missing for ${bamFile}" }
    }


    override fun getResourceFile(): File = bamFile

    override fun toTrackXML() = """        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="${displayMode}" featureVisibilityWindow="-1" fontSize="10" id="${bamFile.absolutePath}" name="${bamFile.name.removeSuffix(".bam")}" sortable="true" visible="true">
            <RenderOptions colorByTag="" colorOption="READ_STRAND" flagUnmappedPairs="false" groupByTag="" maxInsertSize="1000" minInsertSize="50" shadeBasesOption="QUALITY" shadeCenters="true" showAllBases="false" sortByTag=""/>
        </Track>"""
}


class SamTrack(val samFile: File, val isCollapsed: Boolean = true) : IGVTrack() {

    constructor(samFile: String) : this(File(samFile))

    init {
        stopIfNot(samFile.exists()) { "Track file '${samFile}' does not exist" }
        stopIfNot(File(samFile.absolutePath + ".sai").exists()) { "index file is missing for ${samFile}" }
    }


    override fun getResourceFile(): File = samFile

    override fun toTrackXML() = """        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="${displayMode}" featureVisibilityWindow="-1" fontSize="10" id="${samFile.absolutePath}" name="${samFile.name.removeSuffix(".bam")}" sortable="true" visible="true">
            <RenderOptions colorByTag="" colorOption="READ_STRAND" flagUnmappedPairs="false" groupByTag="" maxInsertSize="1000" minInsertSize="50" shadeBasesOption="QUALITY" shadeCenters="true" showAllBases="false" sortByTag=""/>
        </Track>"""
}


class VcfTrack(val vcfFile: File) : IGVTrack() {

    constructor(vcfFile: String) : this(File(vcfFile))

    init {
        stopIfNot(vcfFile.exists()) { "Track file '${vcfFile}' does not exist" }
    }

    override fun getResourceFile(): File = vcfFile

    override fun toTrackXML(): String = """        <Track SQUISHED_ROW_HEIGHT="4" altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" colorMode="GENOTYPE" displayMode="EXPANDED" featureVisibilityWindow="1998000" fontSize="10" id="${vcfFile.absolutePath}" name="${vcfFile.name.removeSuffix(".gz").removeSuffix("vcf")}" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>"""
}

class BedTrack(val bedFile: File) : IGVTrack() {
    constructor(bedFile: String) : this(File(bedFile))

    init {
        stopIfNot(bedFile.exists()) { "Track file '${bedFile}' does not exist" }
    }

    override fun getResourceFile(): File = bedFile

    override fun toTrackXML(): String = """        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" displayMode="${displayMode}" featureVisibilityWindow="1000000" fontSize="10" id="${bedFile.absolutePath}" name="${bedFile.name.removeSuffix(".gz").removeSuffix(".bed")}" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>
"""
}

class BigWigTrack(val bwFile: File) : IGVTrack() {
    constructor(bwFile: String) : this(File(bwFile))

    init {
        stopIfNot(bwFile.exists()) { "Track file '${bwFile}' does not exist" }
    }

    override fun getResourceFile(): File = bwFile

    override fun toTrackXML(): String = """                <Track altColor="0,0,178" autoScale="true" clazz="org.broad.igv.track.DataSourceTrack" color="0,0,178" displayMode="${displayMode}" featureVisibilityWindow="-1" fontSize="10" id="${bwFile.absolutePath}" name="${bwFile.name.removeSuffix(".bw")}" normalize="false" renderer="BAR_CHART" sortable="true" visible="true" windowFunction="mean">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="960.5058" minimum="0.0" panel="LINEAR"/>
        </Track>

    """
}

class GffTrack(val gffFile: File) : IGVTrack() {
    constructor(gffFile: String) : this(File(gffFile))

    init {
        stopIfNot(gffFile.exists()) { "Track file '${gffFile}' does not exist" }
        panel = Panel.Lower
        displayMode = DisplayMode.Expanded
    }

    override fun getResourceFile(): File = gffFile

    override fun toTrackXML(): String = """                <Track altColor="0,0,178" autoScale="true" clazz="org.broad.igv.track.DataSourceTrack" color="0,0,178" displayMode="${displayMode}" featureVisibilityWindow="-1" fontSize="10" id="${gffFile.absolutePath}" name="${gffFile.name.removeSuffix(".gff").removeSuffix(".gtf")}" normalize="false" renderer="BAR_CHART" sortable="true" visible="true" windowFunction="mean">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="960.5058" minimum="0.0" panel="LINEAR"/>
        </Track>

    """
}

fun builSession(genome: String, tracks: List<IGVTrack>): String {

    // auto-expand bed tracks
    tracks.filter { it is BedTrack }.map { it.displayMode = DisplayMode.Expanded }

    val resourceXML = tracks.map { it.getResourceFile() }.map { """<Resource path="${it}"/>""" }.joinToString("\n")

    val (upperTracks, lowerTracks) = tracks.partition { it.panel == Panel.Upper }

    val upperTracksXML = upperTracks.map { it.toTrackXML() }.joinToString("\n")
    val lowerTracksXML = lowerTracks.map { it.toTrackXML() }.joinToString("\n")

    // ensure that the genome is actually present if it's a file
    val isFastaGenome = genome.endsWith(".fasta") || genome.endsWith(".fa")
    if (isFastaGenome) {
        stopIfNot(File(genome).exists()) { "genome '${genome}' does not exist " }
    }else{
        // we can not validate that the user has provided a valid species id, but we can catch most common suffix as errors
        if(listOf("bam", "sam", "bed", "bcf", "vcf", "bw"). contains(File(genome).extension)){
            error("genome '$genome' is malformatted or missing")
        }
    }

    val providedGtfTrack = if (!isFastaGenome) """
        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178"
               colorScale="ContinuousColorScale;0.0;308.0;255,255,255;0,0,178" displayMode="EXPANDED"
               featureVisibilityWindow="-1" fontSize="10" height="35" id="${genome}_genes" name="RefSeq Genes"
               renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="308.0" minimum="0.0" panel="LINEAR"/>
        </Track>
        """ else ""

    return """
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Session genome="${if (File(genome).exists()) File(genome).absolutePath else genome}" hasGeneTrack="${!isFastaGenome}" hasSequenceTrack="true" path="${File(".").absolutePath}${File.pathSeparatorChar}igv_session.xml" version="8">
    <Resources>
       ${resourceXML}
    </Resources>
    <Panel height="962" name="DataPanel" width="2504">
        ${upperTracksXML}
    </Panel>
    <Panel height="137" name="FeaturePanel" width="2504">
        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="Reference sequence" name="Reference sequence" sortable="false" visible="true"/>
        ${lowerTracksXML}
        ${providedGtfTrack}
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
        it.name.endsWith(".sam") -> SamTrack(it)
        it.name.endsWith(".vcf") -> VcfTrack(it)
        it.name.endsWith(".bw") -> BigWigTrack(it)
        it.name.run { endsWith(".gff") || endsWith("gtf") } -> GffTrack(it)
        else -> throw IllegalArgumentException("unsupported track panel")
    }
}

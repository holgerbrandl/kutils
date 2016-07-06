
Example

```{xml}
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Session genome="/Volumes/RADseq-planarian/radseq/genome/schMedP1.fa" hasGeneTrack="false" hasSequenceTrack="true" locus="contig_2709:38776-38816" path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/igv_session_ratio_difference.xml" version="8">
    <Resources>
        <Resource path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP37.dp20.vcf.gz"/>
        <Resource path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP90.dp20.vcf.gz"/>
        <Resource path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/uniq_var_sites_SP37.bed"/>
        <Resource path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/uniq_var_sites_SP90.bed"/>
        <Resource path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP37.bam"/>
        <Resource path="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP90.bam"/>
    </Resources>
    <Panel height="962" name="DataPanel" width="2504">
        <Track altColor="0,0,178" autoScale="true" color="175,175,175" colorScale="ContinuousColorScale;0.0;128.0;255,255,255;175,175,175" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP90.bam_coverage" name="SP90.bam Coverage" showReference="false" snpThreshold="0.2" sortable="true" visible="false">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="21.0" minimum="0.0" type="LINEAR"/>
        </Track>
        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" height="60" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP90.bam_junctions" name="SP90.bam Junctions" sortable="false" visible="false" windowFunction="count">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="60.0" minimum="0.0" type="LINEAR"/>
        </Track>
        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP90.bam" name="SP90.bam" sortable="true" visible="true">
            <RenderOptions colorByTag="" colorOption="READ_STRAND" flagUnmappedPairs="false" groupByTag="" maxInsertSize="1000" minInsertSize="50" shadeBasesOption="QUALITY" shadeCenters="true" showAllBases="false" sortByTag=""/>
        </Track>
        <Track altColor="0,0,178" autoScale="true" color="175,175,175" colorScale="ContinuousColorScale;0.0;83.0;255,255,255;175,175,175" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP37.bam_coverage" name="SP37.bam Coverage" showReference="false" snpThreshold="0.2" sortable="true" visible="false">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="21.0" minimum="0.0" type="LINEAR"/>
        </Track>
        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" height="60" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP37.bam_junctions" name="SP37.bam Junctions" sortable="false" visible="false" windowFunction="count">
            <DataRange baseline="0.0" drawBaseline="true" flipAxis="false" maximum="60.0" minimum="0.0" type="LINEAR"/>
        </Track>
        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="6945249" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/uniq_var_sites_SP37.bed" name="uniq_var_sites_SP37.bed" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>
        <Track altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="5037052" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/uniq_var_sites_SP90.bed" name="uniq_var_sites_SP90.bed" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>
        <Track altColor="0,0,178" autoScale="false" color="0,0,178" displayMode="COLLAPSED" featureVisibilityWindow="-1" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP37.bam" name="SP37.bam" sortable="true" visible="true">
            <RenderOptions colorByTag="" colorOption="READ_STRAND" flagUnmappedPairs="false" groupByTag="" maxInsertSize="1000" minInsertSize="50" shadeBasesOption="QUALITY" shadeCenters="true" showAllBases="false" sortByTag=""/>
        </Track>
        <Track SQUISHED_ROW_HEIGHT="4" altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" colorMode="GENOTYPE" displayMode="EXPANDED" featureVisibilityWindow="1998000" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP37.dp20.vcf.gz" name="SP37.dp20.vcf.gz" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>
        <Track SQUISHED_ROW_HEIGHT="4" altColor="0,0,178" autoScale="false" clazz="org.broad.igv.track.FeatureTrack" color="0,0,178" colorMode="GENOTYPE" displayMode="EXPANDED" featureVisibilityWindow="1998000" fontSize="10" id="/Volumes/RADseq-planarian/radseq/schMedP1_mapping/SP90.dp20.vcf.gz" name="SP90.dp20.vcf.gz" renderer="BASIC_FEATURE" sortable="false" visible="true" windowFunction="count"/>
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
```
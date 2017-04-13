package org.eso.asp.ssap.util

import org.eso.asp.ssap.domain.PosHandler
import spock.lang.Specification
/*
 * This file is part of SSAPServer.
 *
 * SSAPServer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SSAPServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SSAPServer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2017 - European Southern Observatory (ESO)
 */
/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
class VOTableUtilsSpec extends Specification {

	def "Get column mappings from VOTable"() {
		setup:
		def xml = this.class.getResource('/ssap_columns.vot').text

		when:
		def mappings = VOTableUtils.getUtypeToColumnsMappingsFromVOTable(xml)

		then:
		mappings['Char.SpatialAxis.Coverage.Support.Area']  == "s_region"
		mappings['Char.TimeAxis.Coverage.Bounds.Start'] == "t_min"
		mappings['Char.TimeAxis.Coverage.Bounds.Stop']  == "t_max"
	}

	def "Get format metadata from VOTable"() {
		setup:
		def xml = this.class.getResource('/ssap_columns.vot').text
		def handler = new PosHandler()

		when:
		def votable = VOTableUtils.getSSAMetadata([handler], xml)

		then:
		votable == """<VOTABLE version='1.3' xmlns:ssa='http://www.ivoa.net/xml/DalSsap/v1.1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://www.ivoa.net/xml/VOTable/v1.3' xsi:schemaLocation='http://www.ivoa.net/xml/VOTable/v1.3 http://www.ivoa.net/xml/VOTable/VOTable-1.3.xsd'>
  <DESCRIPTION>Lorem ipsum</DESCRIPTION>
  <RESOURCE type='results'>
    <DESCRIPTION>Lorem ipsum</DESCRIPTION>
    <INFO name='QUERY_STATUS'>OK</INFO>
    <INFO name='SERVICE_PROTOCOL' value='1.1'>SSAP</INFO>
    <PARAM name='INPUT:POS' value='' datatype='char' arraysize='*'>
      <DESCRIPTION></DESCRIPTION>
    </PARAM>
    <PARAM name='INPUT:SIZE' value='' datatype='char' arraysize='*'>
      <DESCRIPTION></DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:access_estsize' ucd='meta.code.mime' unit='kB' utype='ssa:Access.Size' value=''>
      <DESCRIPTION>The format of the downloaded file.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:access_url' ucd='meta.ref.url' utype='ssa:Access.Reference' xtype='adql:CLOB' value=''>
      <DESCRIPTION>A URL that can be used to download the data set (ref. access_format)</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:APERTURE' ucd='instr.fov' unit='arcsec' utype='ssa:Char.SpatialAxis.Coverage.Bounds.Extent' value=''>
      <DESCRIPTION>Aperture angular size, in degrees. Set to the width of the slit or the diameter of the fiber. Ref. the APERTURE keyword in the ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:COLLECTION' ucd='meta.id' utype='ssa:DataID.Collection' value=''>
      <DESCRIPTION>The name of the data collection the data set belongs to.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:creator_name' ucd='meta.id.PI' utype='ssa:DataID.Creator' value=''>
      <DESCRIPTION>The Principal Investigator either of the team that provided the data set to ESO through the Phase 3 process, or of the observing team in case the data have been certified by ESO.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:CREATORDID' ucd='meta.id' utype='ssa:DataID.CreatorDID' value=''>
      <DESCRIPTION>The original file name as assigned by the data producer, in form of a IVOA identifier.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:data_source' utype='ssa:DataID.DataSource' value=''>
      <DESCRIPTION>Enumerated type of data sources. One of: pointed, survey, custom. pointed: A pointed observation of a particular astronomical object or field (it corresponds to a phase 3 stream). survey: a dataset which typically covers some region of observational parameter space in a uniform fashion, with as complete as possible coverage in the region of parameter space observed. custom: Data which has been custom processed, e.g., as part of a specific research project (e.g. a large program).</DESCRIPTION>
    </PARAM>
    <PARAM datatype='int' name='OUTPUT:dataset_length' ucd='meta.number' utype='ssa:Dataset.Length' value=''>
      <DESCRIPTION>Number of pixels in the spectrum.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:dataset_title' ucd='meta.title;meta.dataset' utype='ssa:DataID.Title' value=''>
      <DESCRIPTION>A short, human-readable description of a dataset, and should be less than one line of text. The exact contents of Dataset.Title are up to the data provider.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:em_bw' ucd='em.wl;instr.bandwidth' unit='m' utype='ssa:Char.SpectralAxis.Coverage.Bounds.Extent' value=''>
      <DESCRIPTION>Length in meters of the spectrum. Ref. SPEC_BW keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:em_max' ucd='em.wl;stat.max' unit='m' utype='ssa:Char.SpectralAxis.Coverage.Bounds.Stop' value=''>
      <DESCRIPTION>Maximum spectral value observed, expressed in vacuum wavelength in meters; ref. WAVELMAX keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:em_min' ucd='em.wl;stat.min' unit='m' utype='ssa:Char.SpectralAxis.Coverage.Bounds.Start' value=''>
      <DESCRIPTION>Minimum specrtal value observed, expressed in vacuum wavelength in meters; ref. WAVELMIN keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:em_val' ucd='em.wl;instr.bandpass' unit='m' utype='ssa:Char.SpectralAxis.Coverage.Location.Value' value=''>
      <DESCRIPTION>The central wavelength of the spectrum, expressed in meters</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:FLUXCALIB' ucd='meta.code.qual' utype='ssa:Char.FluxAxis.Calibration' value=''>
      <DESCRIPTION>Level of the calibration of the observable (typically flux); one of: UNCALIBRATED, RELATIVE, ABSOLUTE, NORMALIZED. Red. FLUXCAL in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:FORMAT' ucd='meta.code.mime' utype='ssa:Access.Format' value=''>
      <DESCRIPTION>The format of the downloaded file.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:MTIME' ucd='time' value=''>
      <DESCRIPTION>ISO 8601 of the last modification date of the dataset: the highest between the publication date, the obsolete date, the deprecation date.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:positionJ2000' ucd='pos.eq' unit='deg' utype='ssa:Char.SpatialAxis.Coverage.Location.Value' value=''>
      <DESCRIPTION>Equatorial coordinates (FK5/J2000); spectroscopic target position. Ref. RA and DEC keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:PUBDID' ucd='meta.ref.uri;meta.curation' utype='ssa:Curation.PublisherDID' value=''>
      <DESCRIPTION>IVOA dataset identifier for the published data set. It must be unique within the namespace controlled by the publisher.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:rights' utype='ssa:Curation.Rights' value=''>
      <DESCRIPTION>Either public or proprietary</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:s_dec' ucd='pos.eq.dec' unit='deg' utype='obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2' value=''>
      <DESCRIPTION>Equatorial coordinate: Declination (FK5/J2000); spectroscopic target position. Ref. DEC keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:s_ra' ucd='pos.eq.ra' unit='deg' utype='obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1' value=''>
      <DESCRIPTION>Equatorial coordinate: Right Ascension (FK5/J2000); spectroscopic target position. Ref. RA keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:s_region' ucd='pos.outline;obs.field' utype='ssa:Char.SpatialAxis.Coverage.Support.Area' xtype='adql:REGION' value=''>
      <DESCRIPTION>The spatial footprint of the data set. For an ESO spectrum, the returned footprint is the position (s_ra,s_dec).</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:SNR' ucd='stat.snr' utype='ssa:Derived.SNR' value=''>
      <DESCRIPTION>Signal to noise ratio (average)</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:SPECRP' ucd='spect.resolution' utype='ssa:Char.SpectralAxis.ResPower' value=''>
      <DESCRIPTION>The characteristic spectral resolving power (lambda/delta(lambda)) of the data set. Ref. SPECRES keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:t_exptime' ucd='time.duration;obs.exposure' unit='s' utype='ssa:Char.TimeAxis.Coverage.Support.Extent' value=''>
      <DESCRIPTION>Total integration time per pixel (in seconds); ref. EXPTIME keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:t_max' ucd='time.end;obs.exposure' unit='d' utype='ssa:Char.TimeAxis.Coverage.Bounds.Stop' value=''>
      <DESCRIPTION>Stop time in MJD; ref. MJD-END keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:t_mid' ucd='time.epoch' unit='d' utype='ssa:Char.TimeAxis.Coverage.Location.Value' value=''>
      <DESCRIPTION>Midpoint of the observation.</DESCRIPTION>
    </PARAM>
    <PARAM datatype='double' name='OUTPUT:t_min' ucd='time.start;obs.exposure' unit='d' utype='ssa:Char.TimeAxis.Coverage.Bounds.Start' value=''>
      <DESCRIPTION>Start time in MJD; ref MJD-OBS keyword in ESO SDP standard.</DESCRIPTION>
    </PARAM>
    <PARAM arraysize='*' datatype='char' name='OUTPUT:TARGETNAME' ucd='meta.id;src' utype='ssa:Target.Name' value=''>
      <DESCRIPTION>The target name as assigned by the Principal Investigator; ref. Ref. OBJECT keyword in ESO SDP standard. For spectroscopic public surveys, the value shall be set to the survey source identifier, which shall be unique within the survey</DESCRIPTION>
    </PARAM>
  </RESOURCE>
</VOTABLE>"""
	}

}

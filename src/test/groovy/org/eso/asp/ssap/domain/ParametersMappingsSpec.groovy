package org.eso.asp.ssap.domain

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

import spock.lang.Specification

/**
 * @author Vincenzo Forch&igrave (ESO), vforchi@eso.org, vincenzo.forchi@gmail.com
 */
class ParametersMappingsSpec extends Specification {

	public static final String jsonData = """{"metadata":[{"name":"size","description":"lists the size of variable-length columns in the tableset","datatype":"int"},{"name":"column_name","description":"the column name","datatype":"char","arraysize":"64*"},{"name":"datatype","description":"lists the ADQL datatype of columns in the tableset","datatype":"char","arraysize":"64*"},{"name":"description","description":"describes the columns in the tableset","datatype":"char","arraysize":"512*"},{"name":"indexed","description":"an indexed columngo 1 means 1, 0 means 0","datatype":"int"},{"name":"principal","description":"a principal columngo 1 means 1, 0 means 0","datatype":"int"},{"name":"std","description":"a standard columngo 1 means 1, 0 means 0","datatype":"int"},{"name":"table_name","description":"the table this column belongs to","datatype":"char","arraysize":"64*"},{"name":"ucd","description":"lists the UCDs of columns in the tableset","datatype":"char","arraysize":"64*"},{"name":"unit","description":"lists the unit used for column values in the tableset","datatype":"char","arraysize":"64*"},{"name":"utype","description":"lists the utypes of columns in the tableset","datatype":"char","arraysize":"512*"}],"data":[[null,"access_estsize","VARCHAR","The format of the downloaded file.",0,1,1,"ssa","meta.code.mime","kB","ssa:Access.Size"],[null,"access_format","VARCHAR","The format of the downloaded file.",0,1,1,"ssa","meta.code.mime",null,"ssa:Access.Format"],[null,"access_url","CLOB","A URL that can be used to download the data set (ref. access_format)",0,1,1,"ssa","meta.ref.url",null,"ssa:Access.Reference"],[null,"aperture","DOUBLE","Aperture angular size, in degrees. Set to the width of the slit or the diameter of the fiber. Ref. the APERTURE keyword in the ESO SDP standard.",0,1,1,"ssa","instr.fov","arcsec","ssa:Char.SpatialAxis.Coverage.Bounds.Extent"],[64,"data_source","VARCHAR","Enumerated type of data sources. One of: pointed, survey, custom. pointed: A pointed observation of a particular astronomical object or field (it corresponds to a phase 3 stream). survey: a dataset which typically covers some region of observational parameter space in a uniform fashion, with as complete as possible coverage in the region of parameter space observed. custom: Data which has been custom processed, e.g., as part of a specific research project (e.g. a large program). ",0,1,1,"ssa",null,null,"ssa:DataID.DataSource"],[null,"dataset_length","INTEGER","Number of pixels in the spectrum.",0,1,1,"ssa","meta.number",null,"ssa:Dataset.Length"],[64,"dataset_title","VARCHAR","A short, human-readable description of a dataset, and should be less than one line of text. The exact contents of Dataset.Title are up to the data provider.",0,1,1,"ssa","meta.title;meta.dataset",null,"ssa:DataID.Title"],[64,"em_bw","VARCHAR","Length in meters of the spectrum. Ref. SPEC_BW keyword in ESO SDP standard.",0,1,1,"ssa","em.wl;instr.bandwidth","m","ssa:Char.SpectralAxis.Coverage.Bounds.Extent"],[null,"em_max","DOUBLE","Maximum spectral value observed, expressed in vacuum wavelength in meters; ref. WAVELMAX keyword in ESO SDP standard.",0,1,1,"ssa","em.wl;stat.max","m","ssa:Char.SpectralAxis.Coverage.Bounds.Stop"],[null,"em_min","DOUBLE","Minimum specrtal value observed, expressed in vacuum wavelength in meters; ref. WAVELMIN keyword in ESO SDP standard.",0,1,1,"ssa","em.wl;stat.min","m","ssa:Char.SpectralAxis.Coverage.Bounds.Start"],[null,"em_res_power","DOUBLE","The characteristic spectral resolving power (lambda/delta(lambda)) of the data set. Ref. SPECRES keyword in ESO SDP standard.",0,1,1,"ssa","spect.resolution",null,"ssa:Char.SpectralAxis.ResPower"],[64,"em_val","VARCHAR","The central wavelength of the spectrum, expressed in meters",0,1,1,"ssa","em.wl;instr.bandpass","m","ssa:Char.SpectralAxis.Coverage.Location.Value"],[12,"fluxcal","VARCHAR","Level of the calibration of the observable (typically flux); one of: UNCALIBRATED, RELATIVE, ABSOLUTE, NORMALIZED. Red. FLUXCAL in ESO SDP standard.",0,1,1,"ssa","meta.code.qual",null,"ssa:Char.FluxAxis.Calibration"],[64,"MTIME","VARCHAR","ISO 8601 of the last modification date of the dataset: the highest between the publication date, the obsolete date, the deprecation date.",0,1,1,"ssa","time",null,null],[128,"obs_collection","VARCHAR","The name of the data collection the data set belongs to.",0,1,1,"ssa","meta.id",null,"ssa:DataID.Collection"],[92,"obs_creator_did","VARCHAR","The original file name as assigned by the data producer, in form of a IVOA identifier.",0,0,1,"ssa","meta.id",null,"ssa:DataID.CreatorDID"],[64,"obs_creator_name","VARCHAR","The Principal Investigator either of the team that provided the data set to ESO through the Phase 3 process, or of the observing team in case the data have been certified by ESO.",0,1,1,"ssa","meta.id.PI",null,"ssa:DataID.Creator"],[128,"obs_publisher_did","VARCHAR","IVOA dataset identifier for the published data set. It must be unique within the namespace controlled by the publisher.",0,1,1,"ssa","meta.ref.uri;meta.curation",null,"ssa:Curation.PublisherDID"],[null,"positionJ2000","DOUBLE","Equatorial coordinates (FK5/J2000); spectroscopic target position. Ref. RA and DEC keyword in ESO SDP standard.",0,1,1,"ssa","pos.eq","deg","ssa:Char.SpatialAxis.Coverage.Location.Value"],[64,"rights","VARCHAR","Either public or proprietary",0,1,1,"ssa",null,null,"ssa:Curation.Rights"],[null,"s_dec","DOUBLE","Equatorial coordinate: Declination (FK5/J2000); spectroscopic target position. Ref. DEC keyword in ESO SDP standard.",0,1,1,"ssa","pos.eq.dec","deg","obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2"],[null,"s_ra","DOUBLE","Equatorial coordinate: Right Ascension (FK5/J2000); spectroscopic target position. Ref. RA keyword in ESO SDP standard.",0,1,1,"ssa","pos.eq.ra","deg","obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1"],[null,"s_region","REGION","The spatial footprint of the data set. For an ESO spectrum, the returned footprint is the position (s_ra,s_dec).",0,1,1,"ssa","pos.outline;obs.field",null,"ssa:Char.SpatialAxis.Coverage.Support.Area"],[null,"snr","DOUBLE","Signal to noise ratio (average)",0,1,1,"ssa","stat.snr",null,"ssa:Derived.SNR"],[null,"t_exptime","DOUBLE","Total integration time per pixel (in seconds); ref. EXPTIME keyword in ESO SDP standard.",0,1,1,"ssa","time.duration;obs.exposure","s","ssa:Char.TimeAxis.Coverage.Support.Extent"],[null,"t_max","DOUBLE","Stop time in MJD; ref. MJD-END keyword in ESO SDP standard.",0,1,1,"ssa","time.end;obs.exposure","d","ssa:Char.TimeAxis.Coverage.Bounds.Stop"],[null,"t_mid","DOUBLE","Midpoint of the observation.",0,1,1,"ssa","time.epoch","d","ssa:Char.TimeAxis.Coverage.Location.Value"],[null,"t_min","DOUBLE","Start time in MJD; ref MJD-OBS keyword in ESO SDP standard.",0,1,1,"ssa","time.start;obs.exposure","d","ssa:Char.TimeAxis.Coverage.Bounds.Start"],[128,"target_name","VARCHAR","The target name as assigned by the Principal Investigator; ref. Ref. OBJECT keyword in ESO SDP standard. For spectroscopic public surveys, the value shall be set to the survey source identifier, which shall be unique within the survey",0,1,1,"ssa","meta.id;src",null,"ssa:Target.Name"]]}"""

	def "Get column mappings from JSON"() {
		when:
		def mappings = ParameterMappings.parseFromJSON(jsonData)

		then:
		mappings[ParameterMappings.POS] == "s_region"
	}

	def "Get column mappings from XML"() {
		setup:
		def xml = this.class.getResource('/ssap_columns.vot').text

		when:
		def mappings = ParameterMappings.parseFromXML(xml)

		then:
		mappings[ParameterMappings.POS] == "s_region"
	}

}

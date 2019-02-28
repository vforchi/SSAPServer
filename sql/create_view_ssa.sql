drop view dbo.ssa;

create view dbo.ssa                                    
as SELECT
'http://archive.eso.org/datalink/links?ID=ivo://eso.org/ID?' + dp_id + '&eso_download=file'  as access_url,        -- ssa:Access.Reference MAN
CAST('application/x-fits-bintable' as varchar(27))     as FORMAT,            -- ssa:Access.Format MAN
access_estsize                                         as access_estsize,    -- MUST BE IN kB (both for SSA and ObsCore) -- access_estsize -- ssa:Access.Size REC
obs_collection                                         as COLLECTION,
target_name                                            as TARGETNAME,
s_ra                                                   as s_ra,
s_dec                                                  as s_dec,
concat(s_ra, ' ', s_dec)                               as positionJ2000,     -- Char.SpatialAxis.Coverage.Location.Value    MAN
s_region                                               as s_region,
s_fov*3600.                                            as APERTURE,          -- Char.SpatialAxis.Coverage.Bounds.Extent  MAN
em_min                                                 as em_min,
em_max                                                 as em_max,            
em_res_power                                           as SPECRP,            
(em_min + em_max)/2.                                   as em_val,            -- Char.TimeAxis.Coverage.Location.Value MAN
(em_max - em_min)                                      as em_bw,             -- Char.SpectralAxis.Coverage.Bounds.Extent  MAN
snr                                                    as SNR,
t_min                                                  as t_min,             
t_max                                                  as t_max,
(t_min + t_max)/2.                                     as t_mid,             -- Char.TimeAxis.Coverage.Location.Value MAN
t_exptime                                              as t_exptime,         -- Char.TimeAxis.Coverage.Support.Extent  OPT
em_xel                                                 as dataset_length,    -- ssa:Dataset.Length MAN
obs_title                                              as dataset_title,     -- ssa:DataID.Title MAN
(CASE
  WHEN obs_release_date < getdate() THEN 'public'
  ELSE 'proprietary'                                   
  END
)                                                      as rights,
obs_creator_did                                        as CREATORDID,
pi_name                                                as pi_name,           -- ssa:DataID.Creator
obs_publisher_did                                      as PUBDID,
dp_id                                                  as dp_id,
'FK5'                                                  as space_frame,       -- ssa:CoordSys.SpaceFrame.Name  pos.frame
2000.0                                                 as equinox,           -- ssa:CoordSys.SpaceFrame.Equinox time.equinox;pos.frame
'ESO'                                                  as publisher,         -- ssa:Curation.Publisher  meta.curation MAN
CAST(NULL as varchar(15))                              as data_model,        -- ssa:Dataset.DataModel MAN
CAST(NULL as float)                                    as score,             -- ssa:Query.Score
'archival'                                             as creation_type,     -- ssa:DataID.CreationType
bib_reference                                          as curation_reference,
CAST(NULL as varchar(68))                              as flux_ucd,          -- ssa:Char.FluxAxis.Ucd  meta.ucd
CAST(NULL as varchar(68))                              as spec_ucd,          -- ssa:Char.SpectralAxis.Ucd  meta.ucd  
(t_max - t_min)                                        as t_elapsed,         -- ssa:Char.TimeAxis.Coverage.Bounds.Extent  time.duration REC
CAST('absolute' as varchar(12))                        as WAVECALIB,         -- ssa:Char.SpectralAxis.Calibration    meta.code.qual
s_resolution                                           as spatial_resolution -- ssa:Char.SpatialAxis.Resolution    pos.angResolution
FROM ivoa.ObsCore
WHERE dataproduct_type='spectrum';


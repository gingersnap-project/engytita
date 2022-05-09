create table airports (
ident varchar(12) primary key,
type char(1),
name varchar(100),
elevation integer,
continent char(2),
iso_country char(2),
iso_region varchar(8),
municipality varchar(100),
gps_code varchar(12),
iata_code varchar(12),
local_code varchar(12),
coordinates point);

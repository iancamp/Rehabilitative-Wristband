#!/usr/bin/perl -w
use strict;
use Data::Dumper;
use POSIX;
use Time::HiRes qw{time sleep};

my @rows;
foreach my $i (1..500000) {
	my @row = split('\.',sprintf("%.6f",time));
	push(@row,int(rand(126))+1);
	push(@rows,sprintf("(%d,%d,%d)",@row));
	sleep(.001);
}
my $sql = "INSERT INTO test VALUES".join(",",@rows).";";
print "$sql\n";
`sqlite wristband.db '$sql';`;

=cut 
secs=`echo $utc | sed "s/ .*//"`
mili=`echo $utc | sed -e "s/.* //" -e "s/\(....\).*/\1/"`
echo $secs $mili
sqlite wristband.db "INSERT INTO test VALUES($secs,$mili,`shuf -i 1-50 -n 1`); SELECT * FROM test;"

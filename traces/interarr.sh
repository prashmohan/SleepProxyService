for x in "AuverGrid" "Grid5000" "NorduGrid" "DAS2"; do 
    sort -n $x/submittime.txt > $x/submittime-srt.txt;
    python inter.py $x/submittime-srt.txt > $x/int-arr.txt;
    sort -n $x/int-arr.txt > $x/int-arr-srt.txt;
    python inter-1.py $x/int-arr-srt.txt > $x/int-arr-dist1.txt;
    python ~/Scripts/create-cdf.py -f $x/int-arr-dist1.txt > $x/int-arr-dist.txt;
done
gnuplot inter.plot
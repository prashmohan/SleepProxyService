for x in AuverGrid Grid5000 NorduGrid DAS2; do 
    sqlite3 $x/*.[ds]* < burstiness.sql >| $x/burst.txt; 
    sort -r -n $x/burst.txt > $x/burst-srt.txt;
    cat $x/burst-srt.txt | awk '{print $1, 1}' > $x/burst-val.txt;
    python ~/Scripts/create-cdf.py -f $x/burst-val.txt > $x/burst-cdf.txt;
done
gnuplot burstiness.plot
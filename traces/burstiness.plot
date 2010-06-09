set output "burstiness.png"
set xlabel "Percentage of Time"
set ylabel "Number of processors per minute"
set log y
set title "CDF of burstiness in job submission"
set term png size 1024,768 font "Arial,18"

set  style line  5 lt 1 lw 3 
set  style line  6 lt 2 lw 3 
set  style line  7 lt 3 lw 3 
set  style line  8 lt 6 lw 3

plot "AuverGrid/burst-cdf.txt" using 2:1 smooth bezier w l ls 5 t "AuverGrid", "DAS2/burst-cdf.txt" using 2:1 smooth bezier w l ls 6 t "DAS2", "Grid5000/burst-cdf.txt" using 2:1 smooth bezier w l ls 7 t "Grid5000", "NorduGrid/burst-cdf.txt" using 2:1 smooth bezier w l ls 8 t "NorduGrid"

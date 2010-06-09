set output "tod.png"
set xlabel "Time of day (s)"
set ylabel "Number of Jobs"

set title "Job distribution over the day"
set term png size 1024,768 font "Arial,18"

set  style line  5 lt 1 lw 3 
set  style line  6 lt 2 lw 3 
set  style line  7 lt 3 lw 3 
set  style line  8 lt 6 lw 3

plot "AuverGrid/tod-cdf.txt"  using 2:1 smooth bezier w l ls 5 t "AuverGrid", "Grid5000/tod-cdf.txt" using 2:1 smooth bezier w l ls 6 t "Grid5000", "NorduGrid/tod-cdf.txt" using 2:1 smooth bezier w l ls 7 t "NorduGrid", "DAS2/tod-cdf.txt" using 2:1 smooth bezier w l ls 8 t "DAS2"
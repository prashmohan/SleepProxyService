set output "inter.png"
set ylabel "Percentage of Jobs"
set xlabel "Time (s)"
set log x
set title "CDF of job inter arrival time"
set term png size 1024,768 font "Arial,18"
set xrange [1:10000]
set  style line  5 lt 1 lw 3 
set  style line  6 lt 2 lw 3 
set  style line  7 lt 3 lw 3 
set  style line  8 lt 6 lw 3

plot "AuverGrid/int-arr-dist.txt" using 1:2 smooth bezier w l ls 5 t "AuverGrid", "DAS2/int-arr-dist.txt" using 1:2 smooth bezier w l ls 6 t "DAS2", "Grid5000/int-arr-dist.txt" using 1:2 smooth bezier w l ls 7 t "Grid5000", "NorduGrid/int-arr-dist.txt" using 1:2 smooth bezier w l ls 8 t "NorduGrid"

set output "waittime.png"
set xlabel "Percentage of jobs"
set ylabel "Time (s)"

set title "CDF of job wait times"
set term png size 1024,768 font "Arial,18"

set  style line  5 lt 1 lw 3 
set  style line  6 lt 2 lw 3 
set  style line  7 lt 3 lw 3 
set  style line  8 lt 6 lw 3

set yrange [0:10]

plot "AuverGrid/waittime-cdf.txt" using 2:1 w l ls 5 t "AuverGrid" , "Grid5000/waittime-cdf.txt" using 2:1 w l ls 6 t "Grid5000", "DAS2/waittime-cdf.txt" using 2:1 w l ls 7 t "DAS2"
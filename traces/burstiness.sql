select sum(NProc) from Jobs WHERE SubmitTime >= 0 and NProc > 0 GROUP BY SubmitTime/60;
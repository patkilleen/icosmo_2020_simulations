#install.packages("DescTools")
library("MESS")
args <- commandArgs(TRUE)
output_csv <- args[1]
output_roc_curve_file <- args[2]


readOutputData <- function(output_csv){
#experiment id,algorithm,tp,tn,fp,fn,tp rate,fp rate,threshold,accuracy,fscore
 data2 = read.csv(file=output_csv,header=TRUE,sep=",")
 colnames(data2)[1] <- "experiment_id"
 colnames(data2)[2] <- "algorithm_id"
 colnames(data2)[3] <- "algorithm_name"
 colnames(data2)[4] <- "tp"
 colnames(data2)[5] <- "tn"
 colnames(data2)[6] <- "fp"
 colnames(data2)[7] <- "fn"
 colnames(data2)[8] <- "tpr"
 colnames(data2)[9] <- "fpr"
 colnames(data2)[10] <- "threshold"
 colnames(data2)[11] <- "accuracy"
 colnames(data2)[12] <- "fscore"
 
 return (data2)
}

#http://www.endmemo.com/program/R/pchsymbols.php 
#https://www.statmethods.net/graphs/line.html
createROCCurve <- function(output_csv,output_roc_curve_file){

	print(paste0("reading output file: ",output_csv))
	outputData = readOutputData(output_csv)
	outputData = outputData[order(outputData$threshold),]
	#outputData = outputData[order(outputData$algorithm_name),]
	outputData = outputData[order(outputData$algorithm_id),]

	#specify what png output file will be, along with the resolution
	png(file = output_roc_curve_file, res = 1200,width= 3.25,height= 3.25, units = "in", pointsize = 4)
	
	#2 plots ABOVE AND BEWLOW each other
	par(mfrow=c(2,1),mar=c(2,2,2,2))
	
	#this makes factors so algorithms used to id what color to print
	outputData$algorithm_id = factor(outputData$algorithm_id )
	outputData$algorithm_name = factor(outputData$algorithm_name )
	#mycolors = factor(outputData$algorithm,labels =c('red','yellow','blue','green'))
	mycolors =c('green','red','blue','yellow','orange','black')
	mysymboles = c('+','+','*','*','*','+')

	#this one will (type s) put line between poitns
	plot(outputData$fpr,outputData$tpr,type = "p", pch=mysymboles[outputData$algorithm_id], xlim=c(0,1),xlab="False Positive Rate",col=mycolors[outputData$algorithm_id],ylim=c(0,1), ylab="True Positive Rate", main="ROC Curve")#tpr vs fpr

	#plot  the diagnal line of roc curve
	lines(c(0,1), c(0,1), col='black', type="l", lty=2)#type c for dottet line, print line from (0,0) to (1,1) 
	
	
	algNames = unique(outputData$algorithm_name)
	uniqueAlgIds = unique(outputData$algorithm_id)
	
	
	areasUnderCurveVec = vector()
	for(a in uniqueAlgIds){
	
		algData = outputData[outputData$algorithm_id == a,]
		
		x = algData$fpr
		y = algData$tpr
		
		validDataFlag = (countNonNaN(x)>1) && (countNonNaN(y)>1)

		#only calculate area under curve of non null (needs atleast 2 poitns) cuves

		if(validDataFlag){
			areaUnderCurve =  auc(x, y,from = min(x), to = max(x), type = c("linear"), absolutearea = FALSE)
		}else{
			areaUnderCurve = 0
		}
		areasUnderCurveVec = c(areasUnderCurveVec,areaUnderCurve)
			#calculate area under curve
			#print(paste0("area under curve for algorithm (",a,"):" ,areaUnderCurve))
	
	}
	strAUC = format(areasUnderCurveVec, digits = 4)
	barNames = vector()
	
	
	for(i in 1:length(uniqueAlgIds)){
		name = algNames[i]
		auc = strAUC[i]
		newName = paste0(name,": ",auc)
		barNames = c(barNames,newName)
	}
	algNames= order(algNames)
	
	#make a legend of algorithm names
	
		##legend("bottomright", legend = barNames[uniqueAlgIds],
		#col=mycolors[uniqueAlgIds], pch = 15)
	
	
		legend("bottomright", legend = barNames[algNames],
		col=mycolors[algNames], pch = 15)
	
	
	

	#algNames[uniqueAlgIds] + str(areasUnderCurveVec) 
	print(barNames)
	#plot the area under curve comparison
	#barplot(areasUnderCurveVec, main="Area Under ROC Curve", horiz=FALSE, ylim=c(0,1.1), xlab="Algorithm", col = mycolors[uniqueAlgIds])
	barplot(areasUnderCurveVec, main="Area Under ROC Curve", horiz=FALSE, ylim=c(0,1.1), xlab="Algorithm", col = mycolors[algNames])
  
		
	print(paste0("roc curve plotted: ",output_roc_curve_file))
}



countNonNaN <- function(x){

numNonNan = 0
 for(elem in x){
 
	if(is.nan(x) == FALSE){
		numNonNan = numNonNan + 1
	}
 }
 return (numNonNan)
}
createROCCurve(output_csv,output_roc_curve_file)
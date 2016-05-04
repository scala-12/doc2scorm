var parser = new Object;

parser.stack = "";
   
parser.tokensNames = "bnq;-,()re";                 

parser.states = [
//  b  n  q  ;  -  ,  (  )  Er e 
	[0, 1, 1, 0, 0, 0, 1, 0, 0, 0], // begin
	[0, 0, 0, 0, 0, 0, 1, 0, 0, 0], // n
	[0, 0, 0, 1, 1, 1, 0, 1, 0, 0], // q 
	[0, 1, 1, 0, 0, 0, 1, 0, 0, 1], // ;
	[0, 0, 1, 0, 0, 0, 0, 0, 0, 0], // -
	[0, 0, 1, 0, 0, 0, 1, 0, 0, 0], // , 
	[0, 0, 1, 0, 0, 0, 1, 0, 0, 0], // (	
	[0, 0, 0, 1, 0, 1, 0, 1, 0, 1], // )
	[0, 0, 0, 0, 0, 0, 0, 0, 1, 0], // Error
	[0, 0, 0, 0, 0, 0, 0, 0, 0, 0]  // end
];

parser.push = function(t){
 	this.stack = this.stack + t;
	return;
}

parser.pop = function(){
   var result = this.stack;
	this.stack = "";
	return result;
}


parser.isDigit = function(t){
   return ((t >= '0') && (t <= '9'));
}

parser.isNextTokenValid = function(curTokenId, nextTokenId){ // 
	var result = true;
   validStates = this.states[curTokenId];
//   alert("Current token: "+tokensNames.charAt(curTokenId)+" | Next token: "+tokensNames.charAt(nextTokenId)+" | "+validStates+" | "+nextTokenId);
   if (validStates[nextTokenId] == 0) {
		result = false;   
   }
	return result; 
}

parser.lexicalAnalyzer = function(inputLine){
//	alert(inputLine);
   inputLine = "" + inputLine;
   if (inputLine.charAt(inputLine.length-1)!=";") inputLine = inputLine+";";
   var beginElement = new Array("b");
   var tokenList = new Array(beginElement);
	for (var i=0; i<inputLine.length; i++){
//	   alert(inputLine[i]);
		if (this.isDigit(inputLine.charAt(i))){ // digit
			this.push(inputLine.charAt(i));
		}else{ // non digit
         if (i > 0)
				if (this.isDigit(inputLine.charAt(i-1))){
					if (inputLine.charAt(i) == '(')	{
                  var v = parseInt(this.pop());
//                  alert(v);
						tokenList[tokenList.length] = new Array("n", v);
					}else{
                  var v = parseInt(this.pop());
//                  alert(v);
						tokenList[tokenList.length] = new Array("q", v);
					}
				}	
			tokenList[tokenList.length] = new Array(inputLine.charAt(i));
		}
	}
   tokenList[tokenList.length] = new Array("e");
    
/*   for (var i=0; i<tokenList.length; i++){
      var token = tokenList[i];
		//alert(token);
		document.write(token[0]); 
		if (token.length>1){
			//alert(token[1]+"!");
			document.write(" | ");
			document.write(token[1]);
      }

      document.write("<br/>");
   }
   document.close();*/
//   var flag = validityCheck(tokenList);
//	alert(flag);
   //alert(tokenList);
	return tokenList;
}

parser.validityCheck = function(tokens){   
	var result = true;
	for (var i=0; i<tokens.length; i++){ 
		var token = tokens[i];
		var tokenId = this.tokensNames.indexOf(token[0]);
		//alert(tokensNames+" | Token "+token[0]+" | Token idx "+tokenId);
      if (i == (tokens.length-1)) return result;
      var nextToken = tokens[i+1];
		var nextTokenId = this.tokensNames.indexOf(nextToken[0]);
      if (!this.isNextTokenValid(tokenId, nextTokenId)) {
			result = false;
         break;
		}      		                          
	}
   //alert(result);
	return result;
}


// q - element, 	e.g. q; 
// r - range, 		e.g. 10-30; (20-30)
// o - or, 			e.g. 10-30,40-50;(10-30,40-50);
// s - selection, e.g. 4(10-30);5(31-40,40-50);  

parser.parseRange = function(tokens){ // length = 3, e.g. 10-20
//	alert("parseRange "+tokens);
   var result = new Array();
   for (var i=parseInt(tokens[0][1]); i<=parseInt(tokens[2][1]); i++){
		result[result.length] = new Array("q", i);	
		result[result.length] = new Array(";");			
	}
   return result;
}

parser.parseOr = function(tokens){ // 10-29,30-50,(5(10-11,12-13)) 
//	alert("parseOr "+tokens);
	var result = new Array();
	var bracketsCnt = 0 ;
   var buffer = new Array();   
   for (var i = 0; i<tokens.length; i++){
      var token = tokens[i];
      
  		buffer[buffer.length] = token;

      if (token[0] == "("){
			bracketsCnt++;
		} 

      if (token[0] == ")"){
			bracketsCnt--;
		} 

		if (bracketsCnt == 0) {
			if (token[0] == ","){
				buffer.splice(buffer.length-1,1);
            result[result.length] = this.parseSequence(buffer); 
				buffer = new Array();
	   	}
		}  
	}
   result[result.length] = this.parseSequence(buffer); 
   var index = Math.round((result.length-1)*Math.random());
   return result[index];
}

parser.parseSelection = function(tokens){
//	alert("parseSelection "+tokens);
	var result = new Array();
	var bracketsCnt = 0 ;
   var buffer = new Array();   
   /*(for (var i = 1; i<tokens.length; i++){
      var token = tokens[i];
      
  		buffer[buffer.length] = token;

      if (token[0] == "("){
			bracketsCnt++;
		} 

      if (token[0] == ")"){
			bracketsCnt--;
		} 

		if (bracketsCnt == 0) {
			break;
		}  
	}  
   var sequence = this.parseSequence(buffer);*/

   var count = tokens.splice(0,1);
   //alert("Count "+count);
	var sequence = this.parseSequence(tokens);

	for (var i=0; i<sequence.length; i++) {
		if (sequence[i][0]==";")
			 sequence.splice(i,1);
	}

	for (var i=0; i<count[0][1]; i++) {
		var idx = Math.round((sequence.length-1)*Math.random());
	  	result[result.length] = sequence[idx];
		result[result.length] = new Array(";");	  	
		sequence.splice(idx,1);
      
	}
   return result;
}

parser.parseSemicolonSequence = function(tokens){
//	alert("parseSemicolonSequence "+tokens);
	var bracketsCnt = 0;
   var leftPart = new Array();
	var rightPart = new Array();
   var isLeftPart = true;
	for (var i = 0; i<tokens.length; i++){
		var token = tokens[i];
	   if (isLeftPart)
			leftPart[leftPart.length] = tokens[i];
		else
			rightPart[rightPart.length] = tokens[i]; 

      if (token[0] == "(") {
			bracketsCnt++;
		} 

	   if (token[0] == ")"){
			bracketsCnt--;
		}
          
		if ((bracketsCnt == 0)&&(tokens[i][0] == ";")) isLeftPart = false;
	} 	
	
	if (leftPart[leftPart.length-1][0]==";") 
		leftPart.splice(leftPart.length-1,1);	

	var result = new Array();
	result = result.concat(this.parseSequence(leftPart));

	if (rightPart.length>0){
		if (rightPart[rightPart.length-1][0]==";") 
			rightPart.splice(rightPart.length-1,1);	
//      alert("leftPart " + leftPart + "\nrightPart "+rightPart);
		result = result.concat(this.parseSequence(rightPart));
	}
//   alert("leave parseSemicolonSequence "+result);
	return result;
}


parser.parseSequenceInBrackets = function(tokens){
	//alert("parseSequenceInBrackets "+tokens);
/*	var bracketsCnt = 0;	
	
	if ((tokens[0][0] != "(") || (tokens[tokens.length-1][0] != ")"))
		return tokens;

	for (var i = 0; i < tokens.length; i++){
		var token = tokens[i];
      if (token[0] == "(") {
			bracketsCnt++;
		} 

	   if (token[0] == ")"){
			bracketsCnt--;
		}
          
		if ((bracketsCnt == 0)&&(i != (tokens.length-1))) 
			return tokens;
   }
	tokens.splice(0,1);
	tokens.splice(tokens.length-1,1);*/
	return this.parseSequence(tokens.splice(1,tokens.length-2));
}
          
parser.isSemicolonSequence = function(tokens){
	var bracketsCnt = 0;
	var result = false; 

	for (var i = 0; i < tokens.length; i++){
	   var token = tokens[i];

      if (token[0] == "(") {
			bracketsCnt++;
		} 

	   if (token[0] == ")"){
			bracketsCnt--;
		}
          
		if ((bracketsCnt == 0)&&(tokens[i][0] == ";")) {
			result = true;
			break;
		}
			
   }
	return result;
}

parser.isSequenceInBrackets = function(tokens){
	var bracketsCnt = 0;	
	
	if ((tokens[0][0] != "(") || (tokens[tokens.length-1][0] != ")"))
		return false;

	for (var i = 0; i < tokens.length; i++){
		var token = tokens[i];
      if (token[0] == "(") {
			bracketsCnt++;
		} 

	   if (token[0] == ")"){
			bracketsCnt--;
		}
          
		if ((bracketsCnt == 0)&&(i != (tokens.length-1))) 
			return false;
   }
	return true;		
}

parser.isSelection = function(tokens){
	var tmp = new Array();
	for(var i=0;i<tokens.length;i++){
		tmp[tmp.length] = tokens[i];
	}
	if ((tokens[0][0] == "n") && (this.isSequenceInBrackets(tmp.splice(1,tmp.length-1))))
		return true;
	else
		return false;
}

parser.isOr = function(tokens){
	var bracketsCnt = 0;
	var result = false; 

	for (var i = 0; i < tokens.length; i++){
	   var token = tokens[i];

      if (token[0] == "(") {
			bracketsCnt++;
		} 

	   if (token[0] == ")"){
			bracketsCnt--;
		}
          
		if ((bracketsCnt == 0)&&(tokens[i][0] == ",")) {
			result = true;
			break;
		}
			
   }
	return result;
}

parser.parseSequence = function(tokens){
   //alert("parseSequence : "+ tokens);	

	if (tokens.length == 1) 
		return new Array(tokens[0], new Array(";"));

	if ((tokens.length == 2)&&(tokens[1][0]==";")) 
		return tokens;

	if ((tokens.length == 3) && (tokens[1][0]=="-")) 
		return this.parseRange(tokens);


	if (this.isSemicolonSequence(tokens))
		return this.parseSemicolonSequence(tokens);

	if (this.isOr(tokens))
		return this.parseOr(tokens);

	if (this.isSequenceInBrackets(tokens))
		return this.parseSequenceInBrackets(tokens);

	var f = this.isSelection(tokens);
	if (f == true){
		return this.parseSelection(tokens);
	}
	

}
                                    
function generateSequenceArray(seq){
//   var seq = "1;2;10-20;5(21-30,40-50);((51-60),(61-70;72))";
//   var seq = "2(25-31);";

/*	var seq = "1;2;3-5,6-8,9-11,12-14,15-17,18-20,21-23;"+
				"24;2(25-31);32;33;1(34-36);37;1(38-43);2(44-48);49;50;3(51-77);"+
				"1(78-94);1(95-135);1(136-148);2(149-163);2(164-183);"+
				"184-187,188-191,192-195,196-199,200-203,204-207,208-211,212-215;"+
				"1(216-222);1(223-230);1(231-236);1(237-239);1(240-244);1(245-248);"+
				"3(249-260);2(261-273);1(274-283);3(284-309);310;1(311-313);1(314-316);"+
				"1(317-325);2(326-335)";*/
   var line = parser.lexicalAnalyzer(seq);
	if (parser.validityCheck(line)){
		line.splice(0,1);
		line.splice(line.length-1,1);
		var resultSequence = parser.parseSequence(line);
      //alert(resultSequence);
		var result = new Array();

     	for (var i=0; i<resultSequence.length; i++) {
         var a = resultSequence[i];
//			alert(a);
			if (a[0] != ";")
			   result[result.length]=a[1];
         
		}
		return result;
		//alert("Number of questions : "+result.length);
	}else {
		alert("The input sequence is not valid!");
		return null;
	}
			
}

// Table.js
// Dynamic data

// Define sorting order.
var ASC = 0;
var DESC = 1;

var sortOrder = ASC;
var columnSorted = "Last Name";

function Record(firstn, lastn, phone, email, campus, role, dept, fax, office)
{
	this.firstName = firstn;
	this.lastName = lastn;
	this.phoneNumber = phone;
	this.email = email;
	this.campus = campus;
	this.role = role;
	this.department = dept;
	this.fax = fax;
	this.office = office;
}

function Table()
{
	this.rows = [];
	this.columns = [];
}

Table.prototype.addRow = function(rowData)
{
	with (this) {
		row = rowData.split('|');
		
		for (var i = 0; i < 9; i++) {
			row[i] = row[i] || "&nbsp;";
		}
		
		rows[rows.length] = new Record(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8]);
	}
}

Table.prototype.clear = function()
{
	with (this) {
		rows.length = 0;
		if (rows[0]) {
			rows[0] = undefined;
		}
	}
}

Table.prototype.sort = function()
{
	with (this) {
		rows.sort(compareRecords);
	}
}

Table.prototype.generateHTML = function(divObj, delimiter)
{
	with (this) {
		var line;
		
		if (!divObj) {
			return null;
		}
	
		if (!delimiter) {
			delimiter = "|";
		}
		
		sort();
		
		var columns = [
			"Last Name",
			"First Name",
			"Phone",
			//"Email",          // E-mail column is temporarily removed.
			"Campus",
			"Role",
			"Department",
			"Fax",
			"Office"
		];
		
		line = "<table class=\"output\" width=\"100%\"><tr>";
		for (var i = 0; i < columns.length; i++) {
		    if(columnSorted == columns[i]){  //column is already being sort, so sort in the opposite direction
			   if(sortOrder == ASC){         //sorted ascending 
			      line += "<th onMouseOver = \"javascript:highlight(this)\" onMouseOut = \"javascript:unHighlight(this)\" onClick=\"javascript:doSort('" + columns[i] + "')\" class = \"columnHeader\"><a href = \"#\">" + columns[i] + " <img src = \"img/up_arrow.gif\"></a></th>";
			   }
			   else {						//sorted ascending
			      line += "<th onMouseOver = \"javascript:highlight(this)\" onMouseOut = \"javascript:unHighlight(this)\" onClick=\"javascript:doSort('" + columns[i] + "')\" class = \"columnHeader\">" +
					"<a href = \"#\">" + columns[i] + " <img src = \"img/down_arrow.gif\"></a></th>";
			   }
			}
			else {							//columns NOT sorted
			   line += "<th onMouseOver = \"javascript:highlight(this)\" onMouseOut = \"javascript:unHighlight(this)\" onClick=\"javascript:doSort('" + columns[i] + "')\" class = \"columnHeader\"><a href = \"#\">" + columns[i] + "</a></th>";
			}
		}
		line += "</tr>";
		
		if (rows.length == 0) {
			line = "<a class=\"noResults\">*** No results for your search. ***</a>";
		}
		
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var klass = "other";
			var bgtext = "white";
			if (row.campus == "Williston") {
				klass = "williston";
				bgtext = "red";
			}
			else if (row.campus == "Randolph") {
				klass = "randolph";
				bgtext = "green";
			}
			
//			if (row.email.length > 30) {
//				row.email = row.email.replace(/@/, "@<br />");
//			}
			
			line += "<tr class = \"" + klass + " outputrow\">";
			line += "<td class=\"outputrow\">" +
					row.lastName +
					"</td>" +
					"<td class=\"outputrow\">" +
					row.firstName +
					"</td>" +
					"<td class=\"outputrow\">" +
					row.phoneNumber +
					"</td>" +
					//"<td class=\"outputrow\">" +
					//row.email +       // Uncomment this section when e-mails return.
					//"</td>" +
					"<td class=\"outputrow\">" +
					row.campus +
					"</td>" +
					"<td class=\"outputrow\">" +
					row.role +
					"</td>" +
					"<td class=\"outputrow\">" +
					row.department +
					"</td>" +
					"<td class=\"outputrow\">" +
					row.fax +
					"</td>" +
					"<td class=\"outputrow\">" +
					row.office +
					"</td>";
			line += "</tr>";
		}
		
		line += "</table>";
		
		divObj.innerHTML = line;
	}
}

Table.prototype.compareRecords = function(record1, record2)
{
	switch (this.columnSorted) {
		case "Last Name":
			if (record1.lastName < record2.lastName) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.lastName > record2.lastName) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "First Name":
			if (record1.firstName < record2.firstName) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.firstName > record2.firstName) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Phone":
			if (record1.phoneNumber < record2.phoneNumber) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.phoneNumber > record2.phoneNumber) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Email":
			if (record1.email < record2.email) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.email > record2.email) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Campus":
			if (record1.campus < record2.campus) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.campus > record2.campus) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Role":
			if (record1.role < record2.role) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.role > record2.role) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Department":
			if (record1.department < record2.department) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.department > record2.department) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Fax":
			if (record1.fax < record2.fax) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.fax > record2.fax) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
		case "Office":
			if (record1.office < record2.office) {
				return ((sortOrder == ASC) ? -1 : 1);
			}
			else if (record1.office > record2.office) {
				return ((sortOrder == ASC) ? 1 : -1);
			}
			else {
				return 0;
			}
			break;
	}
}

//set globals for sorting
Table.prototype.setForSort = function( columnName ) 
{
  with (this) 
  {  //same column,
    if(columnName == columnSorted)
	{
	   if(sortOrder == ASC)
	      sortOrder = DESC;
	   else
	      sortOrder = ASC;
	}
	else //different column chosen, default is to sort asc
	{
	  columnSorted = columnName;
	  sortOrder = ASC;
	}
	//sort and display!
	generateHTML(document.getElementById('output'));
  }
}


function highlight(element)
{
  element.style.background = "#DDDDDD";
}

function unHighlight(element)
{
  element.style.background = "#FFFFFF";
}
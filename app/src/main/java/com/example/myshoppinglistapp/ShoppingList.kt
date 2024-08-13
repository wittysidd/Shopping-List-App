package com.example.myshoppinglistapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShoppingItem(val id:Int, var name: String, var quantity:Int, var qtyUnit:String, var isEditing:Boolean = false)

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun ShoppingListApp(context: Context) {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    // updates our list if items in UI as soon as added or deleted
    var showDialogue by remember { mutableStateOf(false) }
    var itemName by remember{ mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("")}
    var inputUnit by remember { mutableStateOf("Unit") }
    var expanded by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        // LazyColumn - has indefinite amt of rows, to display big list but renders only the required amount of data, so memory saved
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(30.dp)
        ) {// it = list , item = current item
            items(sItems) {// items= iterates thru all the items in list, and executes the lambda function for each items
                item ->  //checks for each item(if,else)           // ' use this to change the name of "it " to "item"
                if(item.isEditing)
                 { // * EDIT LOGIC *
                     ShoppingItemEditor(item = item, onEditComplete = {
                         editedName, editedQuantity ->                      //parameters -> output(Unit)
                         sItems = sItems.map{it.copy(isEditing = false)}      //copy()ensures that the original list sItems remains unchanged  -- sets all items isEditing to false
                         val editeditem = sItems.find{it.id == item.id}     // now find the item we just edited(item.id) in the list of items(it.id)
                                                                            // and paste the values in it using the let function
                         editeditem?.let{       // ' ? ' bcz the edited item could be empty too , hence let func ensures safety
                             it.name = editedName
                             it.quantity = editedQuantity
                         }
                     })
                 }
                else{ // * NORMAL LIST * else button ready state me hai bas dabane ki deri hai
                    ShoppingListItem(item = item, onEditClick = {
                        // finding out which item we r editing & changing its "isEditing Bool to true"
                        // i.e. it creates a copy of that item with the isEditing property set to true
                        // if the ID of the item matches the ID of the clicked item (it.id == item.id).
                        // Otherwise, it sets isEditing to false.
                        sItems = sItems.map{it.copy(isEditing = it.id == item.id)}
                    },
                        onDeleteClick = {
                            sItems = sItems - item // old list - current item
                        })
                 }

            }
        }
        Button(
            onClick = {showDialogue = true},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(30.dp)
        ) {
            Text("Add Item",fontSize = 20.sp)
            Icon(imageVector = Icons.Default.Add, contentDescription = "add item")
        }

    }
    if(showDialogue){
        AlertDialog(onDismissRequest = { showDialogue = false },
            confirmButton = {   // button is always at bottom thus no worries for writing button code first
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Button(onClick = {
                                    if(itemName.isNotBlank() && inputUnit != "Unit")
                                    {

                                        val newItem = ShoppingItem(
                                            id = sItems.size+1,
                                            name = itemName,
                                            qtyUnit = inputUnit,
                                            quantity = itemQuantity.toIntOrNull()?:1
                                        )
                                        sItems = sItems + newItem
                                        showDialogue = false
                                        itemName = ""
                                        itemQuantity = ""
                                        inputUnit = "Unit"
                                    }
                                    else{

                                        showToast(context, "Enter Valid Name & Unit")
                                    }

                                }) {
                                    Text(text = "Add",fontSize = 15.sp)
                                    Icon(imageVector = Icons.Default.Add, contentDescription ="add item" )
                                }
                                Button(onClick = { showDialogue = false }) {
                                    Text(text = "Cancel",fontSize = 15.sp)
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "cancel")
                                }
                            }
            },
            title = {Text("Add Shopping Item",fontSize = 20.sp)},        // title
            text = {                                    // text is actually body, hence drawing text Fields there
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { (Text("Enter Name")) }
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { (Text("Enter Quantity")) }
                    )
                    Box(contentAlignment = Alignment.Center){
                    Button(onClick = { expanded = true }, modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp)) {
                        Text(text = inputUnit, fontSize = 18.sp)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "DropDown")
                    }
                    DropdownMenu(expanded = expanded,
                        onDismissRequest = {expanded = false}) {
                        DropdownMenuItem(text = { Text("Grams(gm)", fontSize = 14.sp) },
                            onClick = {
                                inputUnit = "gm"
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text("Kilograms(kg)", fontSize = 14.sp) },
                            onClick = {
                                inputUnit = "kg"
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text("Packets(pkt)" ,fontSize = 14.sp) },
                            onClick = {
                                inputUnit = "pkt"
                                expanded = false
                            })
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete:(String,Int) -> Unit)
{
    var editedName by remember { mutableStateOf(item.name)}
    var editedQuantity by remember { mutableStateOf(item.quantity.toString())}
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically)
    {
        Column(modifier = Modifier.weight(2f)){
            OutlinedTextField(
                value = editedName,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                label = {Text("Name")},
                textStyle = TextStyle(color = Color.Blue, fontSize = 16.sp),    // text inside the textField
                colors = TextFieldDefaults.outlinedTextFieldColors(               // still experimental thus dashed
                    focusedBorderColor = Color.Green, // Color of the outline when focused
                    unfocusedBorderColor = Color.Blue))

            OutlinedTextField(
                value = editedQuantity,
                onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                label = {Text("Quantity")},
                textStyle = TextStyle(color = Color.Blue, fontSize = 16.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.Blue)
            )
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName,editedQuantity.toIntOrNull() ?: 1)    // if no quant entered - then 1
        }) {
            Text("Save", fontSize = 12.sp)
        }
    }
}


                // basically short function = lambda fn
@Composable     // onEditClick: () -> Unit = is a lambda function(no return value, but executed when the ShoppingListItem() will be called
fun ShoppingListItem(item: ShoppingItem, onEditClick: ()-> Unit, onDeleteClick:()->Unit ) // like onClick() in Button*()
{
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0xFF018727)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = item.name,
            modifier = Modifier.padding(8.dp),
            fontSize = 15.sp,
            textAlign = TextAlign.Center)       // item.name defined in data class

        Text(text = "Qty: ${item.quantity}${item.qtyUnit}",
            modifier = Modifier.padding(8.dp),
            fontSize = 15.sp,
            textAlign = TextAlign.Center)

        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "edit")
        }
        IconButton(onClick = onDeleteClick) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
        }

    }
}
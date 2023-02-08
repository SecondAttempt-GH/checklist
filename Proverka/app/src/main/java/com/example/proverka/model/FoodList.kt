package com.example.proverka.model

class FoodList {

    private var foods:ArrayList<FoodItem> = arrayListOf<FoodItem>()

    fun getFoods(): ArrayList<FoodItem> {
        return foods
    }

    fun setFood(arr: ArrayList<FoodItem>){
     foods = arr
    }

    fun getItem(index: Int): FoodItem {
        return foods[index]
    }

    fun getSize(): Int{
        return foods.size
    }

    //    fun deleteFood(food: FoodItem){
//        val indexToDelete: Int = foods.indexOfFirst { it.name == food.num }
//        if (indexToDelete == -1) return
//        foods.removeAt(indexToDelete)
//    }
//
    fun add(name: String){
        val Foundedindex: Int = foods.indexOfFirst { it.name == name }
        if (Foundedindex != -1) return
        val food = FoodItem(
            id = foods.size.toLong(),
            name = name,
            num = 1
        )
        foods.add(food)
    }
    fun add(name: String, num: String){
        val Foundedindex: Int = foods.indexOfFirst { it.name == name }
        if (Foundedindex != -1) return
        val food = FoodItem(
            id = foods.size.toLong(),
            name = name,
            num = num.toLong()
        )
        foods.add(food)
    }

    fun remove(foodItem: FoodItem){
        foods.remove(foodItem)
    }

    fun incFood(name: String){
        val indexToInc: Int = foods.indexOfFirst { it.name == name }
        if (indexToInc == -1) return
        foods[indexToInc].num.inc()
    }

    fun incFood(foodItem: FoodItem){
        val indexToInc: Int = foods.indexOfFirst { it.name == foodItem.name }
        if (indexToInc == -1) return
        foods[indexToInc].num = foods[indexToInc].num+1
    }

    fun decFood(name: String){
        val indexToDec: Int = foods.indexOfFirst { it.name == name }
        if (indexToDec == -1) return
        foods[indexToDec].num.dec()
    }

    fun decFood(foodItem: FoodItem){
        val indexToDec: Int = foods.indexOfFirst { it.name == foodItem.name }
        if (indexToDec == -1) return
        if (foods[indexToDec].num.toInt() != 0) {foods[indexToDec].num = foods[indexToDec].num-1}

    }
}
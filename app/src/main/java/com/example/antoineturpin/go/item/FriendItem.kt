package com.example.antoineturpin.go.item


import android.view.View
import android.widget.TextView
import com.example.antoineturpin.go.R
import com.example.antoineturpin.go.model.Friend
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.row_friend.view.*
import android.support.v7.widget.DividerItemDecoration




// classe qui va correspondre à un élément d'une liste
// et qui sera liée (plus tard) à une cellule (ViewHolder)
class FriendItem(var contact: Friend): AbstractItem<FriendItem, FriendItem.FriendViewHolder>() {
    override fun getType(): Int {
        // on retourne un identifiant unique pour un type de cellule
        return R.id.friendName
        return R.id.friendPlace
    }


    // Méthode appelée pour créer une cellule (ex : appelée 11 fois seulement)
    override fun getViewHolder(v: View?): FriendViewHolder {
        return FriendViewHolder(v)
    }

    // On retourne la référance du layout de la cellule à charger
    // Ensuite la méthode getViewHolder sera appelée
    override fun getLayoutRes(): Int {
        return R.layout.row_friend
    }

    // classe "recyclée" correspondant à la cellule graphique (View -> row_contact)
    class FriendViewHolder: FastAdapter.ViewHolder<FriendItem> {

        private var friendName: TextView?
        private var friendPlace: TextView?

        constructor(view: View?) : super(view) {
            //récupération des textviews et compagnie
            friendName = view?.findViewById<TextView>(R.id.friendName)
            friendPlace = view?.findViewById<TextView>(R.id.friendPlace)

        }

        // quand la cellule devra être recyclée
        override fun unbindView(item: FriendItem?) {
            //Nettoyage de la cellule avant ré-utilisation
            friendName?.text = null
            friendPlace?.text = null
        }

        // quand la cellule devra être affichée
        override fun bindView(item: FriendItem?, payloads: MutableList<Any>?) {
            // refresh UI

            val contact = item?.contact // -> Contact? => un objet Contact potentiellement nul

            friendName?.text = contact?.Name
            friendPlace?.text = contact?.Place
        }


    }

}
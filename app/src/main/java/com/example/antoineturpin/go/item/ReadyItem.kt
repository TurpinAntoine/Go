package com.example.antoineturpin.go.item


import android.view.View
import android.widget.TextView
import com.example.antoineturpin.go.R
import com.example.antoineturpin.go.model.Friend
import com.example.antoineturpin.go.model.Ready
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.row_ready.*


// classe qui va correspondre à un élément d'une liste
// et qui sera liée (plus tard) à une cellule (ViewHolder)
class ReadyItem(var contact: Ready): AbstractItem<ReadyItem, ReadyItem.ReadyViewHolder>() {
    override fun getType(): Int {
        // on retourne un identifiant unique pour un type de cellule
        return R.id.friendName
    }

    // Méthode appelée pour créer une cellule (ex : appelée 11 fois seulement)
    override fun getViewHolder(v: View?): ReadyViewHolder {
        return ReadyViewHolder(v)
    }

    // On retourne la référance du layout de la cellule à charger
    // Ensuite la méthode getViewHolder sera appelée
    override fun getLayoutRes(): Int {
        return R.layout.row_ready
    }

    // classe "recyclée" correspondant à la cellule graphique (View -> row_contact)
    class ReadyViewHolder: FastAdapter.ViewHolder<ReadyItem> {

        private var friendName: TextView?

        constructor(view: View?) : super(view) {
            //récupération des textviews et compagnie
            friendName = view?.findViewById<TextView>(R.id.friendName)

        }

        // quand la cellule devra être recyclée
        override fun unbindView(item: ReadyItem?) {
            //Nettoyage de la cellule avant ré-utilisation
            friendName?.text = null
        }

        // quand la cellule devra être affichée
        override fun bindView(item: ReadyItem?, payloads: MutableList<Any>?) {
            // refresh UI

            val contact = item?.contact // -> Contact? => un objet Contact potentiellement nul

            friendName?.text = contact?.Name
        }


    }

}
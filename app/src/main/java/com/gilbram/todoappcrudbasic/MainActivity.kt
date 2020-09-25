package com.gilbram.todoappcrudbasic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.Reference

class MainActivity : AppCompatActivity() {


    private lateinit var databaseRef: DatabaseReference
    private lateinit var cekData: DatabaseReference
    private lateinit var readDataListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseRef = FirebaseDatabase.getInstance().reference

        btn_update.setOnClickListener {
            val kalimatawal = input_nama.text.toString()
            val kalimatupdate = edit_nama.text.toString()
            if (kalimatawal.isBlank() || kalimatupdate.isBlank()){
                toastData("kolom tidak boleh kosong")
            }else{
                updateData(kalimatawal, kalimatupdate)
            }
        }
        btn_tambah.setOnClickListener {
            val nama = input_nama.text.toString()
            if (nama.isBlank()) {
                toastData("kolom wajib di isi")
            } else {
                tambahData(nama)
            }
        }
        btn_hapus.setOnClickListener {
            val nama = input_nama.text.toString()
            if (nama.isBlank()) { toastData("kolom harus di isi")
            } else {
                hapusdata(nama)
            }
        }
        cekDataKalimat()
        //intinya kalau di line(32)datanya tidak terpenuhi maka akan terjadi pembuatan function di line(37)
        //isi function di line(57) akan ada penambahan data yang akan menampilkan pesan jika berhasil (69) dan gagal (72)
        //lalu jika data berhasil maka data itu akan masuk ke dalam data base (87)
        //jika ada data yang sama/double di dalam database maka akan memunculkan pesan(77)

        //tambahan +++(input text di aktifkan di(33) dan menjadikan semua datanya menjadi String() lalu terhubung dengan(34)
        // jadi nanti data yang masuk ke dalam database itu data dari input_text(33))
    }

    private fun tambahData(nama: String) {                                                          //val penambahan data
        val data = HashMap<String, Any>()
        data["nama"] = nama

        val dataListener = object : ValueEventListener {                                            //membuat variable yang bertujuan menambahkan data ke firebase(mungkin)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount < 1) {                                                   //jika data di bawah 1/0 maka akan menambahkan data
                    val tambahdata = databaseRef.child("Daftar Nama")         //penambah data
                        .child(nama)
                        .setValue(data)                                                             //Tetapkan nilainya. Jika ada pengamat yang aktif, nilainya akan dikirimkan kepada mereka. Metode ini harus dipanggil dari utas utama.
                    tambahdata.addOnCompleteListener { task ->                          //jika data berhasil di tambahakan
                        if (task.isSuccessful) {                                                    //if, else (if = jika data telah terpenuhi makan akan menampilkan data selanjutnya
                            toastData("$nama telah di tambahkan ")                           //jika berhasil maka akan memunculkan pesan
                        } else {                                                                    //else = jika data tidak terpenuhi makan akan menampilkan data selanjutnya
                            toastData("$nama gagal di tambahkan")                            //jika string gagal di tambahkan maka akan memunculkan pesan
                        }
                                                                                                    //$nama (55)/(57),,ini artinya data yang sudah di tambahkan di firebase misal  //("$nama" di tambahkan = "program" di tambahkan)//
                    }
                } else {
                    toastData("udah ada yang sama")                                          //jika di database ada yang sama
                }
            }

            override fun onCancelled(p0: DatabaseError) {                                           //jika di batalkan
                toastData("terjadi error")                                                   //pesan singkat
            }
        }
        databaseRef.child("Daftar Nama")                                                  //mengecek data yang ada
            .child(nama)
            .addListenerForSingleValueEvent(dataListener)                                           //menambahkan data

    }
    // intinya di cekdatakalimat di(93) bertujuan untuk mengecek data dari firebase dan mengambil data(100) lalu memberikan datanya ke (97)
    // setelah data di ambil,data itu akan berubah menjadi text (97)

    private fun cekDataKalimat() {                                                                  //mengambil data dari database
        readDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {                                                   //cek data apakah sudah ada di dalam
                    var textData = ""                                                               //memasukan sebuah text di ambil dari id (82) lalu dapat di munculkan di temapat id tdi
                    for (data in snapshot.children) {                                 // menghubungkan ke database
                        val nilai =
                            data.getValue(ModelNama::class.java) as ModelNama                       //get nilai data dari modelnama class
                        textData += "${nilai.Nama} \n"                                              //{x + y = y // x + = y}//di sini kita memanggil model "Nama" lalu menghubungkannya ke textData   $ = untuk sebuah variable
                    }
                    txt_nama.text =
                        textData                                                                    // mengambil ID dari xml
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        cekData =
            databaseRef.child("Daftar Nama")                                              // cek data menuju firebase di table "Daftar Nama"
        cekData.addValueEventListener(readDataListener)                                             //memantau perubahan yang terjadi si table "Daftar Nama"
    }

    private fun toastData(pesan: String) {                                                          //memudahkan pemanggilan toast
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT)
            .show()                                                                                 //(?//)
    }

    override fun onDestroy() {                                                                      //function menghancurkan data
        super.onDestroy()
        cekData.removeEventListener(readDataListener)                                               //bertugas menghapus data

    }                                                                                               // }

    // jadi inti dari hapus data ini dari hapusdata(132) kita mencocokan data yang ada di table daftar nama(137) lalu menghapusnya (139)
    // untuk mengecek data yang akan di hapus nanti(136) lalu mengaktifkan variable datalistener(133) di (156) menggunakan addListenerForSingleValueEvent agar bisa melakukannya sekali
    // +++(mengambil data dari firebase di dalam table lalu memanggilnya dan menghapusnya dengan cara mencocokan data yang ada di table

    private fun hapusdata(nama: String) {                                                           //membuat listener data firebase
        val dataListener = object :
            ValueEventListener {                                                                    //membuat variable yang bertujuan menambahkan data ke firebase(mungkin)
            override fun onDataChange(snapshot: DataSnapshot) {                                     //untuk mengetahui aktifitas data seperti penambahan pengurangan dan perubahan data
                if (snapshot.childrenCount > 0) {                                                   //untuk mengetahui banyak data yang telah di ambil
                    databaseRef.child("Daftar Nama")                                      //jika data tersebut ada maka data tersebut akan di hapus dari table daftar nama
                        .child(nama)
                        .removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                toastData("$nama data telah di hapus")                       //menampilkan pesan jikadata yang ada di (if) terpenuhi

                        }
                }else{
                    toastData(" tidak ada data $nama")                                       //menmpilkan pesan jika data dari(if) tidak terpenuhi
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toastData("tidak dapat megahapus $nama")                                     //menampikan pesan jika tidak bisa menghapus data yang ada di dalam table
            }
        }
        val cekData = databaseRef.child("Daftar Nama")                   //untuk menghapus data kita perlu mengecek data yang ada di dalam table nama
            .child(nama)                                                                            //sedangkan addListenerForSingleValueEvent hanya sekali saja karna di sini kita akan menghapus data
        cekData.addListenerForSingleValueEvent(dataListener)                                        //addvalueeventlistener terus"an menginputkan data yang sama
    }
    private fun updateData(kalimatawal : String, kalimatupdate:String) {
        val dataUpdate = HashMap<String,Any>()                                                      //menyimpan data yang telah di update(160) dengan tipe data string
        dataUpdate ["nama"]= kalimatupdate                                                          //mengupdate data yang tadi kita tambahkan

        val dataListener= object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {                                     //untuk mengetahui aktifitas data seperti penambahan pengurangan dan perubahan data
                if (snapshot.childrenCount > 0){                                                    //untuk mengetahui banyak data yang telah di ambil
                    databaseRef.child("Daftar Nama")                                      //jika data tersebut ada maka data tersebut akan di ubah dari table daftar nama
                        .child(kalimatawal)                                                         //memasukan data yang mau di update/di ubah
                        .updateChildren(dataUpdate)                                                 //bertujuan untuk mengupdate data
                        .addOnCompleteListener {task ->
                            if (task.isSuccessful) toastData("Data telah di update")         //jika if talh terpenuhi maka akan menampilakan pesan berikut
                        }
                }else{
                    toastData("data tidak daapat di update")                                 //jika if tidak terpenuhi maka dia akan meampilkan pesan berikut
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        val dataAsal = databaseRef.child("Daftar Nama")                  //untuk mengupdate data kita perlu mengecek data yang ada di dalam table nama
            .child(kalimatawal)                                                                     //addvalueeventlistener terus"an menginputkan data yang sama
        dataAsal.addListenerForSingleValueEvent(dataListener)                                       //addvalueeventlistener terus"an menginputkan data yang sama
    }
}

// inti dari semuanya (semua function punya tugas masing" untuk menjalankan aplikasinya dan punya kodingan sendiri untuk menyelesaikannya)
// setiap kodingan punya arti yang berbeda-beda jadi tidak dapat membuat satu kesatuan di dalam function yang berbeda/kodingan yang sama di dalam function
// onCreate = berfungsi untuk membuat function" yang di butuhkan di dalam aplikasi

// ini adalah sebuah function yang di buat di dalam on create tadi
// tambahData       = bertugas menambah data ke tabledata (firebase)
// cekdatakaliamat  = bertugas menampilkan data  dari firebase (table nama)
// hapusData        = bertugas menghapus data yang ada di dalam firebase (table nama)
// updateData       = bertugas untuk merubah data yang sudah ada di tabledata (firebase)
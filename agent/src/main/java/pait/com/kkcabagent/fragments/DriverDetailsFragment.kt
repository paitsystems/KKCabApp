package pait.com.kkcabagent.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_driver_details.*
import pait.com.kkcabagent.FirstTimeVehicleEntryActivity
import pait.com.kkcabagent.R
import pait.com.kkcabagent.constant.Constant
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var flag : Int = 0

class DriverDetailsFragment : Fragment(), View.OnClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var lay_pan : LinearLayout
    private lateinit var lay_license : LinearLayout
    private lateinit var lay_pic : LinearLayout
    private lateinit var lay_vehImg : LinearLayout
    private lateinit var btn_save : Button
    private val GALLERY = 1
    private val CAMERA = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_driver_details, container, false)

        lay_pan = view.findViewById(R.id.lay_pan)
        lay_license = view.findViewById(R.id.lay_license)
        lay_pic = view.findViewById(R.id.lay_pic)
        lay_vehImg = view.findViewById(R.id.lay_vehImg)
        btn_save = view.findViewById(R.id.btn_save)

        lay_pan.setOnClickListener(this)
        lay_license.setOnClickListener(this)
        lay_pic.setOnClickListener(this)
        lay_vehImg.setOnClickListener(this)
        btn_save.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var f = Constant.checkFolder(Constant.folder_name)
        f = File(f.absolutePath, "temp.jpg")
        val photoURI = FileProvider.getUriForFile(this!!.context!!, context!!.getPackageName() + ".provider", f)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        when(view!!.id) {
            R.id.lay_pan -> {
                flag = 1
                showPictureDialog()
            }
            R.id.lay_license ->{
                flag = 2
                showPictureDialog()
            }
            R.id.lay_pic -> {
                flag = 3
                showPictureDialog()
            }
            R.id.lay_vehImg -> {
                flag = 4
                showPictureDialog()
            }
            R.id.btn_save -> {
                FirstTimeVehicleEntryActivity.flag = 0
                listener!!.onFragmentInteraction()
            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, contentURI)
                        val path = saveImage(bitmap)
                        when (flag) {
                            1 -> img_pan!!.setImageBitmap(bitmap)
                            2 -> img_license!!.setImageBitmap(bitmap)
                            3 -> img_pic!!.setImageBitmap(bitmap)
                            4 -> img_vehImg!!.setImageBitmap(bitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (requestCode == CAMERA) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                when (flag) {
                    1 -> img_pan!!.setImageBitmap(thumbnail)
                    2 -> img_license!!.setImageBitmap(thumbnail)
                    3 -> img_pic!!.setImageBitmap(thumbnail)
                    4 -> img_vehImg!!.setImageBitmap(thumbnail)
                }
                saveImage(thumbnail)
            }
    }

    private fun saveImage(myBitmap: Bitmap):String {
        Constant.checkFolder(Constant.folder_name)
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 15, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString() +"/"+Constant.folder_name)
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val datetime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.ENGLISH)
            val resultdate = Date(datetime)
            var imagePath = ""
            when (flag) {
                1 -> imagePath = "PAN_"
                2 -> imagePath = "LICENSE_"
                3 -> imagePath = "PIC_"
            }
            imagePath = "$imagePath" + sdf.format(resultdate) + ".jpg"
            val f = File(wallpaperDirectory, imagePath)
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context, arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
            fo.close()
            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = activity as OnFragmentInteractionListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DriverDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}


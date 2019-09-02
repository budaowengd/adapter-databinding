package me.lx.sample.ui.gallery

//class GalleryFragment : Fragment() {
//
//    private lateinit var galleryViewModel: GalleryViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        galleryViewModel =
//            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
//        val textView: TextView = root.findViewById(R.id.text_gallery)
//        galleryViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
//        return root
//    }
//}
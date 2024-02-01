package com.example.lazyeye.exercies

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.example.lazyeye.R
import com.example.traningforproject.Data
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable



class Exercises : Fragment() {

    private lateinit var exe1: CardView
    private lateinit var exe2: CardView
    private lateinit var exe3: CardView
    private lateinit var exe4: CardView
    private lateinit var exe5: CardView
    private lateinit var exe6: CardView

    private lateinit var bnv: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercies, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        callViews()
        bnv.visibility = View.VISIBLE
        val bundle = Bundle()
        exe1.setOnClickListener {
            val items = listOf<Data>(
                Data(
                    1 , "انظر إلى اليمين لمدة ثلاث ثواني ثم إلى اليسار لمدة ثلاث الثواني.\n" +
                            "كرر التمرين ثلاث مرات" , "exercise_image/exe1_1.jpg"
                ) ,
                Data(
                    2 , "انظر إلى الأعلى لمدة ثلاث ثواني ثم إلى الاسفل لمدة ثلاث الثواني.\n" +
                            "كرر التمرين ثلاث مرات" , "exercise_image/exe1_2.jpg"
                ) ,
                Data(
                    3 , "ادر عينيك ثلاث مرات إلى اليمين ثم ثلاث مرات إلى اليسار\n" +
                            "وارمش عدة مرات" , "exercise_image/exe1_3.jpg"
                ) ,
                Data(
                    4 ,
                    "انظر إلى الأعلى يسارًا لمدة ثلاث ثواني ثم إلى الأعلى يمينًا لمدة ثلاث مرات\n" +
                            "كرر التمرين ثلاث مرات." ,
                    "exercise_image/exe1_4.jpg"
                ) ,
                Data(
                    5 ,
                    "افتح عينيك بقوة لمدة عشر ثواني وارمش بإستمرار." ,
                    "exercise_image/exe1_5.jpg"
                ) ,
                Data(6 , "اغلق عينيك بقوة لمدة عشر ثواني ثم استرخي." , "exercise_image/exe1_6.jpg")
            )
            bundle.putSerializable("items" , items as Serializable)
            navigateToExerciseContent(it , bundle)

        }

        exe3.setOnClickListener {
            val items = listOf<Data>(
                Data(1 , "الجلوس على كرسي في وضع مستقيم" , "exercise_image/exe3_1.png") ,
                Data(2 , "وضع إصبع السبابة أمام العينين." , "exercise_image/exe3_2.jpg") ,
                Data(
                    3 ,
                    "تركيز النظر على الإصبع وإبعاده ببطء عن الوجه مع استمرار التركيز عليه" ,
                    "exercise_image/exe3_3.jpg"
                ) ,
                Data(4 , "النظر إلى شيء بعيد في الغرفة عدة ثوانٍ" , "exercise_image/exe3_6.png") ,
                Data(
                    5 ,
                    "العودة إلى النظر على الإصبع مع تقريبه إلى الوجه حتى وصوله إلى الأنف" ,
                    "exercise_image/exe3_3.jpg"
                ) ,
                Data(6 , "النظر مرة أخرى إلى شيء بعيد في الغرفة.\n" , "exercise_image/exe3_6.png") ,
                Data(7 , "تكرار التمرين 3 مرات" , "exercise_image/exe3_7.png")
            )
            bundle.putSerializable("items" , items as Serializable)
            navigateToExerciseContent(it , bundle)
        }
        exe2.setOnClickListener {
            val items = listOf<Data>(
                Data(1 , "النظارات أو العدسات الطبية" , "exercise_image/exe2_1.png") ,
                Data(2 , "رقعة العين\n" , "exercise_image/exe2_2.png") ,
                Data(3 , "حقن البوتوكس (Botox)" , "exercise_image/exe2_3.png") ,
                Data(4 , "تمارين العين" , "exercise_image/exe2_4.png")
            )
            bundle.putSerializable("items" , items as Serializable)
            navigateToExerciseContent(it , bundle)

        }
        exe4.setOnClickListener {
            val items = listOf<Data>(
                Data(1 , "الجلوس باستقامة و إرخاء الكتفين" , "exercise_image/exe4_1.png") ,
                Data(
                    2 ,
                    "إمالة الرأس للخلف قليلًا مع إغلاق العينين\n" ,
                    "exercise_image/exe4_2.jpeg"
                ) ,
                Data(
                    3 ,
                    "وضع إصبعي السبابة والوسطى على العين مع التدليك في الناحية اليمنى مع عقارب الساعة، واليسرى عكس عقارب الساعة، مع مراعاة عدم الضغط على العين" ,
                    "exercise_image/exe4_3.png"
                ) ,
                Data(
                    4 ,
                    "تكرار ذلك 10 مرات، ثم التدليك في الاتجاه المعاكس" ,
                    "exercise_image/exe4_4.png"
                )
            )
            bundle.putSerializable("items" , items as Serializable)
            navigateToExerciseContent(it , bundle)

        }
        exe5.setOnClickListener {
            val items = listOf<Data>(
                Data(
                    1 ,
                    "سوف يتم إضافة بعض التمارين قريبا" ,
                    "exercise_image/exe5.png"
                )
            )
            bundle.putSerializable("items" , items as Serializable)
            navigateToExerciseContent(it , bundle)
        }
        exe6.setOnClickListener {
            val items = listOf<Data>(
                Data(
                    1 ,
                    "تمتاز تمرينات القلم الرصاص \"HBPP\" بأنها تمرينات بصرية بسيطة تعمل على جذب العين لنقطة ثابتة ومتقاربة، فضلًا عن تقوية عضلات العين الكسولة وتعزيز عمل العينين معًا كفريق واحد للرؤية.\n" +
                            "ويمكنك تنفيذها عن طريق إمساك طفلك للقلم الرصاص مع جعل العينين تركز على نقطة واحدة ولتكن الممحاة (الموجودة في نهاية القلم)، ثم قرب القلم ببطء إلى طرف الأنف مع تثبيت العينين عليه، وتوقف في حال عدم وضوح الرؤية." ,
                    "exercise_image/exe6_1.png"
                ) ,
                Data(
                    2 ,
                    "ابتكر طبيب العيون السويسري فريدريك بروك، تمرين يعرف بـ\"Brock String\" والذي يساعد على تنسيق عمل العينين معًا.\n" +
                            "تحتاج إلى خيط سميك طوله 1.5 متر أو 5 أقدام مع وضع 3 حبات بلاستيكية ملونة على أن يكونوا على مسافة متساوية، ثم ثبت أحد أطراف السلسلة في مكان ثابت مثل الكرسي أو إلصاقها على الحائط، وثبت الطرف الثاني بإحكام على أنف طفلك، لتركز العينين على حبة واحدة في كل مرة، ولكن إذا ظهر حرف \"x” أمام طفلك يعني أن إحدى العينين لا تعمل جيدًا\n" ,
                    "exercise_image/exe6_2.png"
                ) ,
                Data(
                    3 ,
                    "يُعرف هذا التمرين بـ \"Barrel cards\" ويعتمد على الرسم مستخدمًا الكروت، ارسم على الكرت الأول براميل مدرجة من الأكبر إلى الأصغر وملونة بالأحمر والثاني براميل مدرجة لونها أخضر، ثم قرب الكرت الأول والثاني بشكل أفقي من أنف طفلك بحيث يكون البرميل الأكبر بالقرب من عين طفلك، في المرة الأولى ارسم برميل واحد على أول كرت وضاعف البراميل في الكرت الثاني إلى اثنين، ساعد طفلك على التركيز لمدة 5 ثواني في كل كرت." ,
                    "exercise_image/exe6_3.png"
                ) ,
                Data(
                    4 ,
                    "يعشق الأطفال حصص الرسم والتلوين، لذا استغل هذا في ابنك وقم بشراء الصور الأبيض في أسود والتي تحتاج إلى التلوين مع تغطية العين السليمة، هذا التمرين يقوي من عضلات العين ومفيد لعيوب الإبصار عمومًا" ,
                    "exercise_image/exe6_4.png"
                )
            )
            bundle.putSerializable("items" , items as Serializable)
            navigateToExerciseContent(it , bundle)
        }
    }

    private fun callViews() {
        exe1 = view?.findViewById(R.id.ex1)!!
        exe2 = view?.findViewById(R.id.ex2)!!
        exe3 = view?.findViewById(R.id.ex3)!!
        exe4 = view?.findViewById(R.id.ex4)!!
        exe5 = view?.findViewById(R.id.ex5)!!
        exe6 = view?.findViewById(R.id.ex6)!!
        bnv = requireActivity().findViewById(R.id.my_bottom_navigation)
    }

    private fun navigateToExerciseContent(view: View , bundle: Bundle) {
        Navigation.findNavController(view)
                .navigate(R.id.action_exerciseFragment_to_exerciseContent , bundle)
        bnv.visibility = View.INVISIBLE

    }

    }

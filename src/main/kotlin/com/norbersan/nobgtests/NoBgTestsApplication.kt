package com.norbersan.nobgtests

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

@SpringBootApplication
class NoBgTestsApplication{
    @PostConstruct
    fun start(){
        /*
        var carvekit: DockerComposeContainer<Nothing>? = null
        try {
            carvekit = DockerComposeContainer<Nothing>(
                File("C:/Users/Norberto Sánchez/IdeaProjects/nobgtests/src/main/kotlin/com/norbersan/nobgtests/docker-compose.cpu.yml")
            ).apply {
                withEnv(mapOf(
                    SegmentationNetwork.PARAMNAME to SegmentationNetwork.TRACER_G7.toString(),
                    PreprocessingMethod.PARAMNAME to PreprocessingMethod.NONE.toString(),
                    PostprocessingMethod.PARAMNAME to PostprocessingMethod.FBA.toString(),
                    SegMask.PARAMNAME to SegMask.SIZE_DEFAULT.toString(),
                    MattingMask.PARAMNAME to MattingMask.SIZE_DEFAULT.toString(),
                    TriMapProbability.PARAMNAME to TriMapProbability.THRESHOLD_DEFAULT.toString(),
                    TrimapDilation.PARAMNAME to TrimapDilation.SIZE_DEFAULT.toString(),
                    TrimapErosion.PARAMNAME to TrimapErosion.ITERATE_DEFAULT.toString(),
                    AuthEnabled.PARAMNAME to AuthEnabled.OFF.toString(),
                    User.USERPARAMNAME to User.ADMIN.user(),
                    User.TOKENPARAMNAME to User.ADMIN.token()
                ))
                withExposedService("carvekit_api", 5000, Wait.forHttp("/").forStatusCode(200))
                withLocalCompose(true)
                withPull(true)
                withOptions("--compatibility")
            }

            */

        var carvekit: DockerComposeContainer<Nothing>? = null
        try {
            var carvekit = CarveKitContainerFactory.create(
                "C:/Users/Norberto Sánchez/IdeaProjects/nobgtests/src/main/kotlin/com/norbersan/nobgtests/docker-compose.cpu.yml",
                SegmentationNetwork.TRACER_G7,
                PreprocessingMethod.NONE,
                PostprocessingMethod.FBA,
                SegMask.SIZE_DEFAULT,
                MattingMask.SIZE_DEFAULT,
                TriMapProbability.THRESHOLD_DEFAULT,
                TrimapDilation.SIZE_DEFAULT,
                TrimapErosion.ITERATE_DEFAULT,
                AuthEnabled.OFF
            )
            carvekit.start()

            carvekit.getServicePort("carvekit_api", 5000)

            carvekit.stop()
            carvekit.close()

        } catch(e: Exception){
            e.printStackTrace()
            carvekit?.stop()
            carvekit?.close()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<NoBgTestsApplication>(*args)
}

enum class SegmentationNetwork(private val text: String){
    U2NET("u2net"),
    TRACER_G7("tracer_b7"),
    BASNET("basnet"),
    DEEPLABV3("deeplabv3");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "SEGMENTATION_NETWORK"
    }
}

enum class PreprocessingMethod(private val text: String){
    NONE("none"),
    STUB("stub");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "PREPROCESSING_METHOD"
    }
}

enum class PostprocessingMethod(private val text: String){
    NONE("none"),
    FBA("fba");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "POSTPROCESSING_METHOD"
    }
}

enum class SegMask(private val text: String){
    SIZE_DEFAULT("640"),
    SIZE_320("320"),
    SIZE_960("960");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "SEG_MASK_SIZE"
    }
}

enum class MattingMask(private val text: String){
    SIZE_DEFAULT("2048"),
    SIZE_512("512"),
    SIZE_1024("1024"),
    SIZE_4096("4096");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "MATTING_MASK_SIZE"
    }
}

enum class TriMapProbability(private val text: String){
    THRESHOLD_DEFAULT("231"),
    THRESHOLD_200("200"),
    THRESHOLD_250("250");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "TRIMAP_PROB_THRESHOLD"
    }
}

enum class TrimapDilation(private val text: String){
    SIZE_DEFAULT("30"),
    SIZE_20("20"),
    SIZE_40("40");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "TRIMAP_DILATION"
    }
}

enum class TrimapErosion(private val text: String){
    ITERATE_DEFAULT("5"),
    ITERATE_3("3"),
    ITERATE_8("8");

    override fun toString() = text
    fun textValues() = values().map { it.text }

    companion object{
        const val PARAMNAME = "TRIMAP_EROSION"
    }
}

enum class AuthEnabled(private val enabled: Boolean){
    ON(true),
    OFF(false);

    override fun toString() = if(enabled) "1" else "0"
    fun toBoolean() = enabled
    companion object{
        const val PARAMNAME = "AUTH_ENABLE"
    }
}

enum class User(private val pair: Pair<String, String>){
    ADMIN(Pair("admin", "token"));

    fun user() = pair.first
    fun token() = pair.second

    companion object{
        const val USERPARAMNAME = "USER"
        const val TOKENPARAMNAME = "TOKEN"
    }
}

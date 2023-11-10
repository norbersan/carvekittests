package com.norbersan.nobgtests

import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

class CarveKitContainerFactory {
    companion object {
        @JvmStatic
        fun create(
            fileName: String,
            segmentationNetwork: SegmentationNetwork = SegmentationNetwork.TRACER_G7,
            preprocessingMethod: PreprocessingMethod = PreprocessingMethod.NONE,
            postprocessingMethod: PostprocessingMethod = PostprocessingMethod.FBA,
            segMask: SegMask = SegMask.SIZE_DEFAULT,
            mattingMask: MattingMask = MattingMask.SIZE_DEFAULT,
            triMapProbability: TriMapProbability = TriMapProbability.THRESHOLD_DEFAULT,
            trimapDilation: TrimapDilation = TrimapDilation.SIZE_DEFAULT,
            trimapErosion: TrimapErosion = TrimapErosion.ITERATE_DEFAULT,
            authEnabled: AuthEnabled = AuthEnabled.OFF,
            userName: String? = null, pass: CharArray? = null
        ): CarveKitContainer {
            val envs = mutableMapOf(
                SegmentationNetwork.PARAMNAME to segmentationNetwork.toString(),
                PreprocessingMethod.PARAMNAME to preprocessingMethod.toString(),
                PostprocessingMethod.PARAMNAME to postprocessingMethod.toString(),
                SegMask.PARAMNAME to segMask.toString(),
                MattingMask.PARAMNAME to mattingMask.toString(),
                TriMapProbability.PARAMNAME to triMapProbability.toString(),
                TrimapDilation.PARAMNAME to trimapDilation.toString(),
                TrimapErosion.PARAMNAME to trimapErosion.toString(),
                AuthEnabled.PARAMNAME to authEnabled.toString(),
            ).apply {
                if (authEnabled.toBoolean()){
                    put(User.USERPARAMNAME, userName!!)
                    put(User.TOKENPARAMNAME, String(pass!!))
                }
            }

            return CarveKitContainer(File(fileName), envs).apply {
                withEnv(envs)
                withExposedService("carvekit_api", 5000, Wait.forHttp("/").forStatusCode(200))
                withLocalCompose(true)
                withPull(true)
                withOptions("--compatibility")
            }

        }
    }

    class CarveKitContainer(
        private val file: File,
        private val envs: Map<String, String>
    ): DockerComposeContainer<Nothing>(
        file
    )
    {
        init{
            if (!file.exists()){
                throw IllegalArgumentException("Missing composer file")
            }
        }

        fun getCarveKitEnvs() = envs.toString()

        fun getCarveKitPort() = getServicePort("carvekit_api", 5000)

        fun getAllLogs() = getContainerByServiceName("carvekit_api").get().logs

        fun getStdErr() = getContainerByServiceName("carvekit_api").get().getLogs(OutputFrame.OutputType.STDERR)

        fun getStdOut() = getContainerByServiceName("carvekit_api").get().getLogs(OutputFrame.OutputType.STDOUT)

        fun isRunning() = getContainerByServiceName("carvekit_api").get().isRunning

    }
}
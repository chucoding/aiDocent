package com.challenge.aidocent.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

public class GoogleDao {
	// 번역
	public static String translateText(String text) throws IOException {
		String return_text = "";
		String projectId = "aidocent-326802";
		String targetLanguage = "ko";
		try (TranslationServiceClient client = TranslationServiceClient.create()) {
			LocationName parent = LocationName.of(projectId, "global");
			TranslateTextRequest request = TranslateTextRequest.newBuilder().setParent(parent.toString())
					.setMimeType("text/plain").setTargetLanguageCode(targetLanguage).addContents(text).build();
			TranslateTextResponse response = client.translateText(request);
			for (Translation translation : response.getTranslationsList()) {
				System.out.printf("Translated text: %s\n", translation.getTranslatedText());
				if (!return_text.isEmpty()) {
					return_text = "," + translation.getTranslatedText();
				}
			}
		}
		return return_text;
	}

	// vision
	public static String detectText(String path) throws IOException {
		String result = "";

		List<AnnotateImageRequest> requests = new ArrayList<>();
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(path));
		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					System.out.format("Error: %s%n", res.getError().getMessage());
					return "";
				}
				result = res.getTextAnnotations(0).getDescription();

			}
		}

		return result;
	}

	// TTS
	public static String synthesizeText(String folder_name, String text) throws Exception {
		UUID uuid = UUID.randomUUID();
		String file_name = "";
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
			SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("ko-KR")
					.setSsmlGender(SsmlVoiceGender.FEMALE).build();

			AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

			ByteString audioContents = response.getAudioContent();
			File Folder = new File(folder_name);
			if (Folder.exists() == false) {
				Folder.mkdirs();
			}
			file_name = uuid.toString() + ".mp3";
			try (OutputStream out = new FileOutputStream(Folder.getPath() + File.pathSeparator + file_name)) {
				out.write(audioContents.toByteArray());
				System.out.println("Audio content written to file \"" + file_name + "\"");
			}
		}
		return file_name;
	}
}

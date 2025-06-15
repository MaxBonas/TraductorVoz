package traductor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Writes the displayed subtitle text to a configurable file.
import traductor.SubtitleFileWriter;

/**
 * Swing based view that shows the translated text as subtitles.
 */

public class TranslatorAppView extends JFrame implements TranslationListener {
	private final JLabel translatedLabel = new JLabel("", SwingConstants.CENTER);
	private final SpeechTranslatorService translatorService;
	private final TranslucentPanel bgPanel;
	private Timer flushTimer, clearTimer, fadeOutTimer, fadeInTimer, bufferTimer;
        private float opacity = 0.0f;

        private final Queue<String> bufferQueue = new LinkedList<>();
        private boolean isDisplayingBuffer = false;

        // Responsible for outputting the displayed subtitles to a text file.
        private final SubtitleFileWriter subtitleWriter = new SubtitleFileWriter();

	private static final long serialVersionUID = 1L;
	private static final int CLEAR_MILLIS = 6000;
	private static final int FADE_INTERVAL = 50;
	private static final int FADE_DURATION = 500;
	private static final int MAX_VISIBLE_LINES = 3;
	private static final int BUFFER_DELAY_MILLIS = 4000;

	public TranslatorAppView(SpeechTranslatorService translatorService) {
		this.translatorService = translatorService;
		this.translatorService.setTranslationListener(this);

		setTitle("Subtítulo en Vivo");
		setUndecorated(true);
		setAlwaysOnTop(true);
		setBackground(new Color(0, 0, 0, 0));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.90);
		int height = 100;
		int xOffset = (int) (screenSize.width * 0.05);
		setSize(width, height);
		setLocation(xOffset, screenSize.height - height);

		translatedLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		translatedLabel.setForeground(new Color(255, 255, 255, 0));
		translatedLabel.setOpaque(false);
		translatedLabel.setHorizontalAlignment(SwingConstants.CENTER);

		bgPanel = new TranslucentPanel(new Color(0, 0, 0, 170));
		bgPanel.setAlpha(0);
		bgPanel.setLayout(new BorderLayout());
		bgPanel.add(translatedLabel, BorderLayout.CENTER);

                setContentPane(bgPanel);
                getRootPane().setOpaque(false);

                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                translatorService.stopTranslation();
                        }
                });
        }

	private void restartClearTimer() {
		if (clearTimer != null && clearTimer.isRunning())
			clearTimer.stop();
		clearTimer = new Timer(CLEAR_MILLIS, e -> startFadeOut());
		clearTimer.setRepeats(false);
		clearTimer.start();
	}

	private String formatAsHtmlLines(String text) {
		return "<html><div style='text-align: center; padding-top: 10px; padding-bottom: 10px'>" + text
				+ "</div></html>";
	}

	private void startFadeIn() {
		if (fadeInTimer != null && fadeInTimer.isRunning())
			fadeInTimer.stop();

		opacity = 0.0f;
		int steps = FADE_DURATION / FADE_INTERVAL;
		fadeInTimer = new Timer(FADE_INTERVAL, null);

		fadeInTimer.addActionListener(e -> {
			opacity += 1.0f / steps;
			if (opacity >= 1.0f) {
				translatedLabel.setForeground(new Color(255, 255, 255, 255));
				bgPanel.setAlpha(170);
				bgPanel.repaint();
				fadeInTimer.stop();
			} else {
				translatedLabel.setForeground(new Color(255, 255, 255, Math.min(255, (int) (opacity * 255))));
				bgPanel.setAlpha(Math.min(170, (int) (opacity * 170)));
				bgPanel.repaint();
			}
		});
		fadeInTimer.start();
	}

	private void startFadeOut() {
		if (fadeOutTimer != null && fadeOutTimer.isRunning())
			fadeOutTimer.stop();

		opacity = 1.0f;
		int steps = FADE_DURATION / FADE_INTERVAL;
		fadeOutTimer = new Timer(FADE_INTERVAL, null);

		fadeOutTimer.addActionListener(e -> {
			opacity -= 1.0f / steps;
			if (opacity <= 0.0f) {
				translatedLabel.setText("");
				translatedLabel.setForeground(new Color(255, 255, 255, 0));
				bgPanel.setAlpha(0);
				bgPanel.repaint();
				fadeOutTimer.stop();
			} else {
				translatedLabel.setForeground(new Color(255, 255, 255, Math.max(0, (int) (opacity * 255))));
				bgPanel.setAlpha(Math.max(0, (int) (opacity * 170)));
				bgPanel.repaint();
			}
		});
		fadeOutTimer.start();
	}

	private void cancelFadeAnimations() {
		if (fadeOutTimer != null) fadeOutTimer.stop();
		if (fadeInTimer != null) fadeInTimer.stop();
		translatedLabel.setForeground(new Color(255, 255, 255, 255));
		bgPanel.setAlpha(170);
		bgPanel.repaint();
	}

	@Override
	public void onPartialResult(String original, String translated) {}

        @Override
        public void onFinalResult(String original, String translated) {
                SwingUtilities.invokeLater(() -> {
                        if (translated == null || translated.trim().isEmpty()) return;

                        List<String> blocks = splitTextIntoBlocks(translated.trim());
                        bufferQueue.addAll(blocks);
                        if (!isDisplayingBuffer) {
                                displayNextBlockFromQueue();
                        }
                });
        }


	private List<String> splitTextIntoBlocks(String fullText) {
		List<String> blocks = new ArrayList<>();
		FontMetrics fm = translatedLabel.getFontMetrics(translatedLabel.getFont());
		int maxWidth = getWidth() - 60;
		List<String> lines = new ArrayList<>();
		StringBuilder currentLine = new StringBuilder();

		for (String word : fullText.split(" ")) {
			String testLine = currentLine + (currentLine.length() == 0 ? "" : " ") + word;
			if (fm.stringWidth(testLine) > maxWidth) {
				lines.add(currentLine.toString());
				currentLine = new StringBuilder(word);
			} else {
				currentLine.append((currentLine.length() == 0 ? "" : " ") + word);
			}
		}
		if (currentLine.length() > 0) lines.add(currentLine.toString());

		for (int i = 0; i < lines.size(); i += MAX_VISIBLE_LINES) {
			StringBuilder block = new StringBuilder();
			for (int j = i; j < i + MAX_VISIBLE_LINES && j < lines.size(); j++) {
				block.append(lines.get(j)).append("<br>");
			}
			blocks.add(block.toString());
		}
		return blocks;
	}

	private void displayNextBlockFromQueue() {
		if (bufferQueue.isEmpty()) {
			isDisplayingBuffer = false;
			return;
		}
		isDisplayingBuffer = true;

                String block = bufferQueue.poll();
                String htmlText = formatAsHtmlLines(block);
                translatedLabel.setText(htmlText);
                subtitleWriter.write(block.replace("<br>", System.lineSeparator()));
		
		SwingUtilities.invokeLater(() -> {
			Dimension preferredSize = translatedLabel.getPreferredSize();
			int padding = 40;
			int newHeight = preferredSize.height + padding;

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) (screenSize.width * 0.90);
			int xOffset = (int) (screenSize.width * 0.05);

			setSize(width, newHeight);
			setLocation(xOffset, screenSize.height - newHeight);
			});
		
		startFadeIn();
		restartClearTimer();

		bufferTimer = new Timer(BUFFER_DELAY_MILLIS, e -> displayNextBlockFromQueue());
		bufferTimer.setRepeats(false);
		bufferTimer.start();
	}

        @Override
        public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                        translatedLabel.setText("Error: " + message);
                        cancelFadeAnimations();
                        if (flushTimer != null) flushTimer.stop();
                        if (clearTimer != null) clearTimer.stop();
                        if (bufferTimer != null) bufferTimer.stop();
                });
        }

        @Override
        public void onSessionStopped() {
                SwingUtilities.invokeLater(() -> {
                        cancelFadeAnimations();
                        if (flushTimer != null) flushTimer.stop();
                        if (clearTimer != null) clearTimer.stop();
                        if (bufferTimer != null) bufferTimer.stop();
                        translatedLabel.setText("");
                        bufferQueue.clear();
                        isDisplayingBuffer = false;
                });
        }

	static class TranslucentPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Color baseColor;
		private int alpha = 170;

		public TranslucentPanel(Color baseColor) {
			this.baseColor = baseColor;
			setOpaque(false);
		}

		public void setAlpha(int alpha) {
			this.alpha = alpha;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha));
			g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
			g2d.dispose();
			super.paintComponent(g);
		}
	}
}

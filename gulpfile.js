var gulp = require('gulp');
var series = require('stream-series');
var inject = require("gulp-inject");
var mainBowerFiles = require('main-bower-files');

gulp.task('inject', function() {
	var target = gulp.src('./src/main/resources/templates/head.html');
	
	var vendorStream = gulp.src(['./js/libs/**/*.js','./js/libs/**/*.css', './css/styles.css'], {read: false, cwd: './src/main/resources/static'});

	return target.pipe(inject(vendorStream, {selfClosingTag: true})).pipe(
			gulp.dest('./src/main/resources/templates'));
});

gulp.task('bower', function() {
	return gulp.src(mainBowerFiles())
			.pipe(gulp.dest('./src/main/resources/static/js/libs'));
});
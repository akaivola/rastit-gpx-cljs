var parseFeatureVector = function(k) {
  var s = []
  var h = k.split("\n")
  var p = 0
  var m = 0
  var l = 0
  var u = 0
  var o = 0
  var j = 0
  var c = 0
  var e = 1000000
  var n = 10
  var b = 1 / 1000
  for (var g = 0; g < h.length - 1; g++) {
    var r = h[g].split(" ")
    p += parseInt(r[0], 10)
    m += parseInt(r[1], 10)
    l += parseInt(r[2], 10)
    u += parseFloat(r[3])
    if (g > 0) {
      o += parseInt(r[4], 10)
      if (r.length > 5) {
        j += parseInt(r[5], 10)
      } else {
        j += parseInt(r[4], 10) - parseInt(r[2], 10)
      }
      if (r.length > 6) {
        c += parseInt(r[6], 10)
      }
    }
    var q = new OpenLayers.Geometry.Point(p / e,m / e)
    q.z = l / n;
    q.t = u / b;
    q.ascension = o / n;
    q.descent = j / n;
    if (r.length > 6) {
      q.distance = c
    }
    s.push(q);
  }
  var f = new OpenLayers.Geometry.LineString(s);
  var featureVector = new OpenLayers.Feature.Vector(f);
  featureVector.geometry.transform(new OpenLayers.Projection("EPSG:4326"), 'EPSG:3857');
  return featureVector;
}

var parseFeature = function(data) {
  var bs = data.split(';')[0].split(':')[1];
  var featureVector = parseFeatureVector(bs);
  var bo = featureVector.geometry.components;
  var by = 0;
  var bp = 0;
  while (by < bo.length - 1) {
    if (bo[by + 1].t + bp < bo[by].t) {
      bp += bo[by].t - (bo[by + 1].t + bp)
    }
    if (bp > 0) {
      bo[by + 1].t += bp
    }
    by++
  }
  var by = 0;
  while (by < bo.length) {
    var bx = 0;
    while ((by + bx + 1 < bo.length) && (((bo[by].x == bo[by + bx + 1].x) && (bo[by].y == bo[by + bx + 1].y)) || (bo[by].t == bo[by + bx + 1].t))) {
      bx++
    }
    bo.splice(by + 1, bx);
    by++
  }

  var feature = new ol.Feature({
    geometry: new ol.geom.LineString(
                featureVector.geometry.components.map(function(coordinate) {
                  return [coordinate.x, coordinate.y]}))})
  feature.getGeometry().transform(ol.proj.get('EPSG:3857'), 'EPSG:4326')
  return feature
}

var toGpx = function(feature) {
  return new ol.format.GPX().writeFeatures([feature])
}

var createElementAndDownload = function(name) {
  return function(data) {
    var link = document.createElement('a')
    link.setAttribute('href', 'data:application/gpx+xml;charset=utf-8,' + data)
    link.setAttribute('download', name + '.gpx')

    if (document.createEvent) {
      var event = document.createEvent('MouseEvents')
      event.initEvent('click', true, true)
      link.dispatchEvent(event)
    } else link.click()
  }
}

var fetchGpx = (number, name) => {
  fetch("https://cors-anywhere.herokuapp.com/https://www.rastit.fi/ajax/download-routes.php?id=" + number)
    .then(response => response.text())
    .then(parseFeature)
    .then(toGpx)
    .then(encodeURIComponent)
    .then(createElementAndDownload(name || number))
}

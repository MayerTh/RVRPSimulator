X <- c(1:100)
dodY <- c(0.49,0.58,0.46,0.41,0.63,0.45,0.54,0.51,0.51,0.53,0.39,0.55,0.46,0.54,0.49,0.47,0.42,0.47,0.48,0.5,0.4,0.49,0.55,0.5,0.52,0.45,0.47,0.49,0.57,0.45,0.49,0.54,0.43,0.53,0.49,0.48,0.45,0.59,0.5,0.53,0.47,0.46,0.56,0.51,0.5,0.53,0.36,0.49,0.5,0.46,0.51,0.55,0.44,0.53,0.5,0.39,0.49,0.62,0.54,0.53,0.43,0.48,0.51,0.54,0.47,0.44,0.51,0.41,0.54,0.46,0.53,0.58,0.48,0.46,0.52,0.48,0.48,0.46,0.43,0.49,0.52,0.43,0.48,0.51,0.43,0.55,0.53,0.41,0.49,0.52,0.45,0.53,0.55,0.47,0.56,0.47,0.47,0.42,0.49,0.42)
edodY <- c(0.07892,0.06778,0.09719000000000001,0.10859999999999997,0.06503,0.10148,0.07729,0.09036000000000001,0.07833,0.07592,0.09228,0.07573000000000002,0.08421999999999999,0.07617,0.08933999999999997,0.08742000000000003,0.10134999999999998,0.09356000000000002,0.08455,0.07512000000000002,0.10074999999999998,0.08778000000000002,0.08617999999999998,0.07583,0.09593,0.08159000000000001,0.09074,0.09131999999999998,0.07226999999999999,0.10065999999999999,0.09133999999999999,0.08066,0.10051000000000002,0.07328000000000003,0.07956999999999999,0.08552000000000001,0.09770000000000001,0.06045999999999998,0.08893000000000001,0.07412,0.07813999999999999,0.09863,0.05992,0.08366,0.09327000000000002,0.08253,0.10855000000000004,0.08725999999999998,0.08020000000000001,0.08795,0.09311999999999998,0.07082000000000002,0.08067,0.06435999999999999,0.08315,0.10082000000000002,0.08285000000000001,0.06756000000000001,0.07411,0.07486000000000001,0.09418999999999998,0.08437,0.07421,0.06952999999999998,0.09336000000000003,0.08289999999999996,0.08213999999999999,0.11098999999999998,0.07321000000000003,0.09613,0.07838999999999999,0.07003999999999999,0.07896,0.08314000000000002,0.07659000000000002,0.07298999999999999,0.09120000000000004,0.09689999999999996,0.08593,0.09201999999999998,0.08335,0.10193000000000002,0.08272,0.09255000000000001,0.10459,0.06952,0.07314000000000002,0.0915,0.08072999999999998,0.07728,0.08318999999999997,0.08098000000000001,0.08424,0.08726999999999999,0.08268000000000005,0.07413,0.07572000000000001,0.10344000000000003,0.09258,0.09837999999999997)
dedodY <- c(0.46137124247540445,0.32625694843140135,0.599484729875288,0.6598199023540058,0.28452663393154753,0.5792321752912458,0.382688545612761,0.4435466778327466,0.6481423428789375,0.4069044717564929,0.5969401018436867,0.4284245437172847,0.6849456451204212,0.43351859769132106,0.500798238517955,0.39199129488574536,0.5830117724002616,0.4594966437868388,0.5254328924394128,0.46593709651135096,0.6461654726126635,0.5247693241447131,0.525649546249988,0.4335422953156142,0.33961860440236435,0.44475912391674516,0.5642529151476359,0.5113507512047624,0.3050622954712417,0.6445819660436581,0.5697879228706715,0.5930874901681026,0.5362775803216384,0.3605370935466928,0.3953185824643498,0.5639388035782342,0.6354930436089723,0.40402041866170707,0.41491978294207854,0.45882175378578255,0.5171443427457599,0.5788012930501245,0.7386179197869572,0.3266069086642957,0.4416123778501629,0.3579180593424934,0.5279240934126148,0.41293363081580875,0.46523508442354916,0.5536484694970001,0.2851298922995748,0.42383041132385196,0.5553657833755568,0.4295364248998745,0.38676985895309235,0.6958658493775821,0.6307718959460114,0.35732784778749965,0.5469038201145074,0.36457756924275814,0.6032629456140164,0.573208826582604,0.29372082968415686,0.4198524726007456,0.4071156160482736,0.5183365085405146,0.3939542044427067,0.5611282704512406,0.5797405224427161,0.4330491354220168,0.5831956349616165,0.5177111502627025,0.5660784584570757,0.6097719756444334,0.41349992045913436,0.48152343960727195,0.6970671196871594,0.5818716761807917,0.6407492104976588,0.422903856780724,0.5147888377950155,0.5786510806854387,0.6755776917312016,0.405145290925328,0.6130728741367727,0.5731935410641847,0.3833315302645102,0.5400057233512732,0.5548739188966962,0.3319279518506632,0.6113995290672505,0.6186350225026174,0.5180400818451862,0.46920704986911793,0.3367357345369461,0.41589916454434506,0.6335341674884944,0.5733279159723386,0.3491333905420613,0.6269689231649276)
wedodY <- c(0.4542610374265697,0.4326901917799119,0.6008242414176121,0.6625916949114353,0.3898797556198358,0.6143238017173912,0.5191505141699827,0.46404833836858006,0.46519090544722186,0.4021733733022155,0.6608591865886266,0.3833428133686847,0.6683048394461686,0.33375097017407696,0.5273982416457031,0.4192621799405171,0.4034535571217787,0.5465035255794481,0.5615621210861694,0.5503005907163534,0.6642140832788315,0.6067216389377208,0.44859320433130534,0.43537238832853026,0.36205872611610457,0.5697345367441887,0.5098388699718145,0.5362578792533056,0.3811974789915966,0.6098395383021985,0.5476647595234906,0.434975127371926,0.6454576333992095,0.3482700566780383,0.5754055451050181,0.4035025625053091,0.5489557672420317,0.2903839316940917,0.5546396027640643,0.5977472170871923,0.4967312116855763,0.5264105514361204,0.4216681102562796,0.3603472939021112,0.5959996533495103,0.46790112446396587,0.6481933672785123,0.4989525368248772,0.5858535629793711,0.43601300428550316,0.4527983806023232,0.49360423637697326,0.6808834167860537,0.5407063543902212,0.494981680914844,0.527081747779256,0.5333708051589223,0.245509057921272,0.3765556793104164,0.29959078747180107,0.6741486823554929,0.4950986075150391,0.6765395617327962,0.4995777001181216,0.5417923171864463,0.5628166859643718,0.45250186880956833,0.5828000503172933,0.5283115592929991,0.4706804290580533,0.4451584695252761,0.41631981718992284,0.7197373481411223,0.5572261743345902,0.5598299337761721,0.5272659091761353,0.4852991471551027,0.5530719509010186,0.5081467756426797,0.4982763832975561,0.31749908665574583,0.40243823273567636,0.5147809189698513,0.3665867002640617,0.6866352302334288,0.48771656700751276,0.5472503447759758,0.666523265028869,0.4628355158307346,0.419105931264443,0.5758995601228336,0.4071613038319257,0.4857642484847456,0.5605616524077001,0.39427270773617284,0.5919454642007804,0.6560434687928804,0.515085394105921,0.4654326053211789,0.42448576222104756)
plot(X, dodY, type="l", main="Test", ylim=c(0,1.2), col="green", xlab="DVRP Instances", ylab="Degree")
lines(X, edodY, col="red")
lines(X, dedodY, col="blue")
lines(X, wedodY, col="black")
legend(0,1.2,c("DOD", "EDOD", "D-DOD", "W-DOD"), col=c("green", "red", "blue", "black"), lty=1)
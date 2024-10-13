import React from "react";
import { Box, Card, Grid } from "@mui/material";
import colorConfigs from "../../configs/colorConfigs";
import { Link } from "react-router-dom";

const Home = () => {
  return (
    <Card
      component="main"
      sx={{
        flexGrow: 1,
        p: 2,
        widht: "100%",
        height: "80vh",
        marginX: "2rem",
        padding: "2rem",
        backgroundColor: colorConfigs.mainBg,
        border: "1px solid black",
      }}
    >
      <Grid container  rowGap={10} sx={{ flexGrow: 1 }}>
        <Grid xs={4} component={Link} to={"/e2j"}>
          <Box
            width={150}
            height={80}
            sx={{
              bgcolor: "#26355D",
              color: "white",
              boxShadow: 1,
              borderRadius: 2,
              textAlign: "center",
              alignContent: "center",
            }}
          >
            Excel 2 Json
          </Box>
        </Grid>

        <Grid xs={4} component={Link} to={""}>
          <Box
            width={150}
            height={80}
            sx={{
              bgcolor: "#26355D",
              color: "white",
              boxShadow: 1,
              borderRadius: 2,
              textAlign: "center",
              alignContent: "center",
            }}
          >
            2
          </Box>
        </Grid>

        <Grid xs={4} component={Link} to={"/"}>
          <Box
            width={150}
            height={80}
            sx={{
              bgcolor: "#26355D",
              color: "white",
              boxShadow: 1,
              borderRadius: 2,
              textAlign: "center",
              alignContent: "center",
            }}
          >
            3
          </Box>
        </Grid>

        <Grid xs={4} component={Link} to={""}>
          <Box
            width={150}
            height={80}
            sx={{
              bgcolor: "#26355D",
              color: "white",
              boxShadow: 1,
              borderRadius: 2,
              textAlign: "center",
              alignContent: "center",
            }}
          >
            4
          </Box>
        </Grid>

        <Grid xs={4} component={Link} to={""}>
          <Box
            width={150}
            height={80}
            sx={{
              bgcolor: "#26355D",
              color: "white",
              boxShadow: 1,
              borderRadius: 2,
              textAlign: "center",
              alignContent: "center",
            }}
          >
            5
          </Box>
        </Grid>

        <Grid xs={4} component={Link} to={""}>
          <Box
            width={150}
            height={80}
            sx={{
              bgcolor: "#26355D",
              color: "white",
              boxShadow: 1,
              borderRadius: 2,
              textAlign: "center",
              alignContent: "center",
            }}
          >
            6
          </Box>
        </Grid>
      </Grid>
    </Card>
  );
};

export default Home;
